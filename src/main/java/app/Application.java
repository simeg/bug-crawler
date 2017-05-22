package app;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.crawl.Crawler;
import app.parse.HtmlParser;
import app.parse.Parser;
import app.persist.Persister;
import app.persist.PsqlPersister;
import app.plugin.HtmlComments;
import app.plugin.PageFinder;
import app.plugin.Plugin;
import app.plugin.Wordpress;
import app.queue.PersistentQueue;
import app.queue.QueueSupervisor;
import app.request.RequestImpl;
import app.request.Requester;
import app.request.UrlRequest;
import app.util.Utilities;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static app.util.Utilities.isBlacklisted;

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
    // QueueSupervisor takes three persisters
    // This is not optimal, not sure how to fix it though.
    final PsqlPersister persister = getPersister(conf);
    final QueueSupervisor supervisor = QueueSupervisor.create(persister, persister, persister);

    final HashMap<String, Object> requestCache = Maps.newHashMap();
    final RequestImpl requestImpl = new RequestImpl();
    final Requester requester = new Requester(requestImpl, supervisor.requests(), requestCache);

    final Parser parser = HtmlParser.create();
    final ExecutorService executor = Executors.newFixedThreadPool(50);
    app.start(initUrl, supervisor, executor, parser, persister, requester);
  }

  void start(
      String initUrl,
      QueueSupervisor supervisor,
      ExecutorService executor,
      Parser parser,
      Persister persister,
      Requester requester) {
    supervisor.addToCrawl(initUrl);

    submitWorkerNTimes(10, "Crawler", executor, supervisor.subLinks(), (String urlToCrawl) -> {
      LOG.info("Starting crawl thread with name: {}", Thread.currentThread().getName());

      final String fixedUrl = Utilities.normalizeProtocol(urlToCrawl.toLowerCase());

      if (!Utilities.isValidUrl(fixedUrl)) {
        LOG.info(
            "{}: Consumed URL is invalid - skipping: {}",
            Thread.currentThread().getName(), fixedUrl);
        return;
      } else if (isBlacklisted(Utilities.getDomain(fixedUrl))) {
        LOG.info("{}: URL is blacklisted - skipping: {}", Thread.currentThread().getName(), fixedUrl);
        return;
      }

      final Set<String> subLinks = new Crawler(requester, parser).getSubLinks(fixedUrl);

      // URL is crawled and ready to be analyzed
      supervisor.addToAnalyze(fixedUrl);

      if (subLinks.size() > 0) {
        // Add sub-links back on URL queue
        supervisor.addToCrawl(subLinks);

        LOG.info(
            "{}: Found {} sub-links for: {}",
            Thread.currentThread().getName(), String.valueOf(subLinks.size()), fixedUrl);
      } else {
        LOG.info(
            "{}: No sub-links found for: {}",
            Thread.currentThread().getName(), fixedUrl);
      }
    });

    submitWorkerNTimes(10, "Analyzer", executor, supervisor.crawledLinks(), (String urlToAnalyze) -> {
      if (urlToAnalyze != null) {
        LOG.info("Starting analyze thread with name: {}", Thread.currentThread().getName());

        final List<Plugin> plugins = Arrays.asList(
            new HtmlComments(parser),
            new Wordpress(parser),
            new PageFinder(parser)
        );

        final Analyzer analyzer = new Analyzer(plugins);
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

    submitRequestWorkers(10, "Requester", executor, supervisor.requests(), requester);
  }

  private void submitRequestWorkers(
      final int times,
      String threadName,
      ExecutorService executor,
      PersistentQueue<UrlRequest> queue,
      Requester requester) {
    for (int i = 0; i < times; i++) {
      final int number = i;
      executor.submit(() -> {
        final String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(threadName + "-" + number);

        while (true) {
          try {
            final UrlRequest urlRequest = queue.poll(10, TimeUnit.SECONDS);

            final Object result = requester.request(urlRequest.url);

            urlRequest.future.complete(result);

          } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
//            LOG.error("{}: Error requesting url={}", Thread.currentThread().getName(), urlRequest.url, e.toString());
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      });
    }
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
            final T url = queue.poll(10, TimeUnit.SECONDS);

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

}
