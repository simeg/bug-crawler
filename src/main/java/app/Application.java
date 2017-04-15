package app;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.crawl.Crawler;
import app.parse.HtmlParser;
import app.persist.PsqlPersister;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SpringBootApplication(scanBasePackages = {"app"})
public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  private static Config conf;
  private static QueueSupervisor supervisor;
  private static ExecutorService executor;
  private static Map<String, String> dbConfig;

  public static void main(String[] args) throws IOException, InterruptedException {
    final Application app = new Application();

    // TODO: Get this from a website form
    final String initUrl = "http://www.vecka.nu";

    app.init();
    SpringApplication.run(app.getClass(), args);
    app.start(initUrl);
  }

  void init() {
    conf = ConfigFactory.load();

    dbConfig = new HashMap<>();
    dbConfig.put("port", String.valueOf(conf.getInt("db.port")));
    dbConfig.put("host", conf.getString("db.host"));
    dbConfig.put("name", conf.getString("db.name"));
    dbConfig.put("username", conf.getString("db.username"));
    dbConfig.put("password", conf.getString("db.password"));

    PsqlPersister<Bug> bugPersister = PsqlPersister.create(
        "org.postgresql.Driver",
        dbConfig.get("host"),
        Integer.parseInt(dbConfig.get("port")),
        dbConfig.get("name"),
        dbConfig.get("username"),
        dbConfig.get("password"));
    PsqlPersister<String> persister = PsqlPersister.create(
        "org.postgresql.Driver",
        dbConfig.get("host"),
        Integer.parseInt(dbConfig.get("port")),
        dbConfig.get("name"),
        dbConfig.get("username"),
        dbConfig.get("password"));
    supervisor = QueueSupervisor.create(bugPersister, persister);
    executor = Executors.newFixedThreadPool(50);
  }

  void start(String initUrl) {
    supervisor.subLinks().add(initUrl);

    submitWorkerNTimes(10, executor, supervisor.subLinks(), urlToCrawl -> {
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

        final Analyzer analyzer = new Analyzer(new HtmlParser(), conf.getList("analyzer.filePaths").unwrapped());
        final Set<Bug> bugs = analyzer.analyze(urlToAnalyze);

        supervisor.bugs().addAllBugs(bugs);
      }
    });

    submitWorkerNTimes(10, executor, supervisor.bugs(), bug -> {
      if (bug != null) {
        LOG.info("Starting persister thread with name: {}", Thread.currentThread().getName());

        final PsqlPersister<Bug> bugPersister = PsqlPersister.create(
            "org.postgresql.Driver",
            dbConfig.get("host"),
            Integer.parseInt(dbConfig.get("port")),
            dbConfig.get("name"),
            dbConfig.get("username"),
            dbConfig.get("password"));
        bugPersister.storeBug(bug);
      }
    });
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

  boolean isBlacklisted(List<Object> blacklist, String domain) {
    return blacklist.contains(domain);
  }

}
