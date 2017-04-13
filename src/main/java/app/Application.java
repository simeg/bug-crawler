package app;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.crawl.Crawler;
import app.parse.HtmlParser;
import app.persist.Persister;
import app.persist.PsqlPersister;
import app.queue.PersistentQueue;
import app.queue.QueueSupervisor;
import app.util.Utilities;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SpringBootApplication(scanBasePackages = {"app"})
public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) throws IOException, InterruptedException {
    final Application app = new Application();

    // TODO: Get this from a website form
    final String initUrl = "http://www.vecka.nu";

    app.start(initUrl);
//    SpringApplication.run(app.getClass(), args);
  }

  void start(String initUrl) {
    // Load DB configurations
    final Config conf = ConfigFactory.load();
    final int port = conf.getInt("db.port");
    final String host = conf.getString("db.host");
    final String name = conf.getString("db.name");
    final String username = conf.getString("db.username");
    final String password = conf.getString("db.password");

    // TODO: Only use ONE Persister
    final Persister<Bug> bugPersister = PsqlPersister.create("org.postgresql.Driver", host, port, name, username, password);
    final Persister<String> persister = PsqlPersister.create("org.postgresql.Driver", host, port, name, username, password);

    final QueueSupervisor supervisor = QueueSupervisor.create(bugPersister, persister);

    final List<Object> blacklist = conf.getList("crawler.blacklist").unwrapped();

    // TODO: Replace when it's possible to provide URL
    supervisor.subLinks().add(initUrl);

    final ExecutorService executor = Executors.newFixedThreadPool(50);

    submitWorkerNTimes(10, executor, supervisor.subLinks(), urlToCrawl -> {
      LOG.info("Starting crawl thread with name: {}", Thread.currentThread().getName());

      final String fixedUrl = Utilities.normalizeProtocol(urlToCrawl.toLowerCase());

      if (!Utilities.isValidUrl(fixedUrl)) {
        LOG.info(
            "{}: Consumed URL is invalid - skipping: {}",
            Thread.currentThread().getName(), fixedUrl);
        return;
      } else if (blacklist.contains(Utilities.getDomain(fixedUrl))) {
        LOG.info("{}: URL is blacklisted, will not do anything more: {}", Thread.currentThread().getName(), fixedUrl);
        return;
      }

      final Set<String> subLinks = new Crawler(new HtmlParser()).getSubLinks(fixedUrl);

      // URL is crawled and ready to be analyzed
      supervisor.crawledLinks().add(fixedUrl);

      if (subLinks.size() > 0) {
        // Add sub-links back on URL queue
        supervisor.subLinks().addAll(subLinks);

        LOG.info(
            "{}: Found {} sub-links: {}",
            Thread.currentThread().getName(), String.valueOf(subLinks.size()), subLinks);
      } else {
        LOG.info(
            "{}: No sub-links found for: {}",
            Thread.currentThread().getName(), fixedUrl);
      }
    });

    submitWorkerNTimes(10, executor, supervisor.crawledLinks(), urlToAnalyze -> {
      if (urlToAnalyze != null) {
        LOG.info("Starting analyze thread with name: {}", Thread.currentThread().getName());

        final Analyzer analyzer = new Analyzer(new HtmlParser());
        final Set<Bug> bugs = analyzer.analyze(urlToAnalyze);

        supervisor.bugs().addAllBugs(bugs);
      }
    });

    submitBugWorkerNTimes(10, executor, supervisor.bugs(), bugPersister);
  }

  // QUESTION: Why doesn't the bug queue get cleared?
  // QUESTION: Generalise this somehow?
  private void submitBugWorkerNTimes(
      final int times,
      ExecutorService executor,
      PersistentQueue<Bug> queue,
      Persister<Bug> persister) {
    for (int i = 0; i < times; i++) {
      executor.submit(() -> {

        final String oldName = Thread.currentThread().getName();
        // TODO: Set appropriate thread name
        Thread.currentThread().setName("hello");

        while (true) {
          try {
            Bug bug = queue.poll(10, TimeUnit.SECONDS);

            persister.storeBug(bug);

          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error(
                "{}: Polling was interrupted: {}",
                Thread.currentThread().getName(), e.toString());
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      });
    }
  }

  private <T> void submitWorkerNTimes(
      final int times,
      ExecutorService executor,
      PersistentQueue<T> queue,
      Consumer<T> jobToDo) {
    for (int i = 0; i < times; i++) {
      executor.submit(() -> {
        final String oldName = Thread.currentThread().getName();
        // TODO: Set appropriate thread name
        Thread.currentThread().setName("hello");

        while (true) {
          try {
            T url = queue.poll(10, TimeUnit.SECONDS);

            jobToDo.accept(url);

          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error(
                "{}: Polling was interrupted: {}",
                Thread.currentThread().getName(), e.toString());
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      });
    }
  }
}
