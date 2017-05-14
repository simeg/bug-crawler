package app;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.crawl.Crawler;
import app.parse.HtmlParser;
import app.parse.Parser;
import app.persist.Persister;
import app.persist.PsqlPersister;
import app.plugin.HtmlInspector;
import app.plugin.Plugin;
import app.queue.PersistentQueue;
import app.queue.QueueSupervisor;
import app.util.Utilities;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Arrays;
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

    app.init(app);
    SpringApplication.run(app.getClass(), args);
  }

  void init(Application app) {
    // TODO: Get this from a website form
    final String initUrl = "http://www.vecka.nu";

    final Config conf = ConfigFactory.load();
    // QUESTION:
    // QueueSupervisor takes two persisters - one for String and one for Bug.
    // This is not optimal, not sure how to fix it though.
    final PsqlPersister persister = getPersister(conf);
    final QueueSupervisor supervisor = QueueSupervisor.create(persister, persister);

    final ExecutorService executor = Executors.newFixedThreadPool(50);
    final Parser parser = HtmlParser.create();
    app.start(initUrl, supervisor, executor, conf, parser, persister);
  }

  void start(
      String initUrl,
      QueueSupervisor supervisor,
      ExecutorService executor,
      Config conf,
      Parser parser,
      Persister persister
  ) {
    supervisor.addToCrawl(initUrl);

    submitWorkerNTimes(10, "Crawler", executor, supervisor.subLinks(), (String urlToCrawl) -> {
      LOG.info("Starting crawl thread with name: {}", Thread.currentThread().getName());

      final String fixedUrl = Utilities.normalizeProtocol(urlToCrawl.toLowerCase());

      if (!Utilities.isValidUrl(fixedUrl)) {
        LOG.info(
            "{}: Consumed URL is invalid - skipping: {}",
            Thread.currentThread().getName(), fixedUrl);
        return;
      } else if (isBlacklisted(conf.getList("crawler.blacklist").unwrapped(), Utilities.getDomain(fixedUrl))) {
        LOG.info("{}: URL is blacklisted - skipping: {}", Thread.currentThread().getName(), fixedUrl);
        return;
      }

      final Set<String> subLinks = new Crawler(parser).getSubLinks(fixedUrl);

      // URL is crawled and ready to be analyzed
      supervisor.addToAnalyze(fixedUrl);

      if (subLinks.size() > 0) {
        // Add sub-links back on URL queue
        supervisor.addToCrawl(subLinks);

        LOG.info(
            "{}: Found {} sub-links: {}",
            Thread.currentThread().getName(), String.valueOf(subLinks.size()), subLinks);
      } else {
        LOG.info(
            "{}: No sub-links found for: {}",
            Thread.currentThread().getName(), fixedUrl);
      }
    });

    submitWorkerNTimes(10, "Analyzer", executor, supervisor.crawledLinks(), (String urlToAnalyze) -> {
      if (urlToAnalyze != null) {
        LOG.info("Starting analyze thread with name: {}", Thread.currentThread().getName());

        final List<Plugin> plugins = Arrays.asList(new HtmlInspector(parser));

        final Analyzer analyzer = Analyzer.create(parser, conf.getList("analyzer.filePaths").unwrapped(), plugins);
        final Set<Bug> bugs = analyzer.analyze(urlToAnalyze);

        supervisor.addToPersist(bugs);
      }
    });

    submitWorkerNTimes(10, "Persister", executor, supervisor.bugs(), (Bug bug) -> {
      if (bug != null) {
        LOG.info("Starting persister thread with name: {}", Thread.currentThread().getName());

        persister.storeBug(bug);
      }
    });
  }

  private <T> void submitWorkerNTimes(
      final int times,
      String threadName,
      ExecutorService executor,
      PersistentQueue<T> queue,
      Consumer<T> jobToDo) {
    for (int i = 0; i < times; i++) {
      final int number = i;
      executor.submit(() -> {
        final String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(threadName + "-" + number);

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

  private PsqlPersister getPersister(Config conf) {
    return PsqlPersister.create(
        "org.postgresql.Driver",
        conf.getString("db.host"),
        conf.getInt("db.port"),
        conf.getString("db.name"),
        conf.getString("db.username"),
        conf.getString("db.password"));
  }

  static boolean isBlacklisted(List<Object> blacklist, String domain) {
    return blacklist.contains(domain);
  }

}
