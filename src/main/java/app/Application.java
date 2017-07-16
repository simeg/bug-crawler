package app;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.crawl.Crawler;
import app.parse.HtmlParser;
import app.parse.Parser;
import app.persist.Persister;
import app.plugin.HtmlComments;
import app.plugin.Plugin;
import app.plugin.SubPageFinder;
import app.plugin.Wordpress;
import app.queue.QueueId;
import app.queue.QueueSupervisor;
import app.queue.SimpleQueue;
import app.request.JsoupRequester;
import app.request.Requester;
import app.request.UrlRequest;
import app.util.Utilities;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static app.util.Utilities.isBlacklisted;

@Component
public class Application {

  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  public boolean isRunning = false;

  public void init(String url) {
    isRunning = true;

    final Config conf = ConfigFactory.load();
    final Persister persister = getPersister(conf);

    final QueueSupervisor supervisor = QueueSupervisor.create(persister);

    final HashMap<String, Connection.Response> requestCache = Maps.newHashMap();
    final Requester requester =
        new JsoupRequester(supervisor.get(QueueId.TO_BE_REQUESTED), requestCache);

    final Parser parser = HtmlParser.create();
    final ExecutorService executor = Executors.newFixedThreadPool(50);
    start(url, supervisor, executor, parser, requester, persister);
  }

  private void start(
      String url,
      QueueSupervisor supervisor,
      ExecutorService executor,
      Parser parser,
      Requester requester,
      Persister persister) {
    supervisor.get(QueueId.TO_BE_CRAWLED).add(url);

    submitWorkerNTimes(10, "Crawler", executor, supervisor.get(QueueId.TO_BE_CRAWLED),
        (String urlToCrawl) -> {
          LOG.info("Started crawl thread with name: {}", Thread.currentThread().getName());

          final String fixedUrl = Utilities.normalizeProtocol(urlToCrawl);

          if (!Utilities.isValidUrl(fixedUrl)) {
            LOG.info("Consumed URL is invalid - skipping: {}", fixedUrl);
            return;
          } else if (isBlacklisted(Utilities.getDomain(fixedUrl))) {
            LOG.info("URL is blacklisted - skipping: {}", fixedUrl);
            return;
          }

          final Set<String> subLinks = new Crawler(requester, parser).getSubLinks(fixedUrl);

          // URL is crawled and ready to be analyzed
          supervisor.get(QueueId.TO_BE_ANALYZED).add(fixedUrl);

          if (subLinks.size() > 0) {
            // Add sub-links back on URL queue
            subLinks.forEach(link -> supervisor.get(QueueId.TO_BE_ANALYZED).add(link));

            LOG.info("Found {} sub-links for: {}", String.valueOf(subLinks.size()), fixedUrl);
          } else {
            LOG.info("No sub-links found for: {}", fixedUrl);
          }
        });

    submitWorkerNTimes(10, "Analyzer", executor, supervisor.get(QueueId.TO_BE_ANALYZED),
        (String urlToAnalyze) -> {
          if (urlToAnalyze != null) {
            LOG.info("Started analyze thread with name: {}", Thread.currentThread().getName());

            final List<Plugin> plugins = Arrays.asList(
                new HtmlComments(requester, parser),
                new Wordpress(requester),
                new SubPageFinder(requester)
            );

            final Analyzer analyzer = new Analyzer(plugins);
            final Set<Bug> bugs = analyzer.analyze(urlToAnalyze);

            bugs.forEach(bug -> supervisor.get(QueueId.TO_BE_STORED_AS_BUG).add(bug));
          }
        });

    submitWorkerNTimes(10, "Persister", executor, supervisor.get(QueueId.TO_BE_STORED_AS_BUG),
        (Bug bug) -> {
          if (bug != null) {
            LOG.info("Started persister thread with name: {}", Thread.currentThread().getName());

            persister.storeBug(bug);
          }
        });

    submitRequestWorkers(10, executor, supervisor.get(QueueId.TO_BE_REQUESTED), requester);
  }

  @SuppressWarnings("unchecked")
  private void submitRequestWorkers(
      final int times,
      ExecutorService executor,
      SimpleQueue<UrlRequest> queue,
      Requester requester) {
    for (int i = 0; i < times; i++) {
      final int number = i;
      executor.submit(() -> {
        try {
          final String oldName = Thread.currentThread().getName();
          Thread.currentThread().setName("Requester-" + number);
          LOG.info("Started requester thread with name: {}", Thread.currentThread().getName());

          while (true) {
            try {
              final UrlRequest urlRequest = queue.poll(10, TimeUnit.SECONDS);
              if (urlRequest != null) {
                final Optional<?> requestValue = requestType(requester, urlRequest);

                if (requestValue.isPresent()) {
                  urlRequest.future.complete(requestValue.get());
                } else {
                  urlRequest.future.completeExceptionally(
                      new RuntimeException("Future did not succeed, most likely because the request returned a 404"));
                }
              }

            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              LOG.warn("Polling was interrupted: {}", e);
              break;
            }
          }

          Thread.currentThread().setName(oldName);
        } catch (Throwable e) {
          LOG.error("Worker failed", e);
        }
      });
    }
  }

  private <T> void submitWorkerNTimes(
      final int times,
      String threadName,
      ExecutorService executor,
      SimpleQueue<T> queue,
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
            LOG.warn("Polling was interrupted: {}", e);
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      });
    }
  }

  private Optional<?> requestType(Requester requester, UrlRequest request) {
    switch (request.type) {
      case HTML:
        return requester.requestHtml(request.url);
      case HTML_HASH:
        return requester.requestHtmlHashCode(request.url);
      case STATUS_CODE:
        return requester.requestStatusCode(request.url);
      default:
        LOG.warn("Unknown request type=[{}]", request.type);
        break;
    }

    return Optional.empty();
  }

  private Persister getPersister(Config conf) {
    return Persister.create(
        "org.postgresql.Driver",
        conf.getString("db.host"),
        conf.getInt("db.port"),
        conf.getString("db.name"),
        conf.getString("db.username"),
        conf.getString("db.password"));
  }

}
