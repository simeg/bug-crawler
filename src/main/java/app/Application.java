package app;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.crawl.Crawler;
import app.parse.HtmlParser;
import app.parse.Parser;
import app.persist.Persister;
import app.plugin.*;
import app.queue.QueueId;
import app.queue.QueueSupervisor;
import app.request.JsoupRequester;
import app.request.Requester;
import app.request.UrlRequest;
import app.util.Utilities;
import app.work.UrlWorker;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // Add initial URL
    supervisor.get(QueueId.TO_BE_CRAWLED).add(url);

    initRequester(executor, supervisor, requester);
    initCrawler(executor, supervisor, requester, parser);
    initAnalyzer(executor, supervisor, requester, parser);
    initPersister(executor, supervisor, persister);
  }

  @SuppressWarnings("unchecked")
  private void initRequester(
      ExecutorService executor,
      QueueSupervisor supervisor,
      Requester requester) {
    new UrlWorker<>("Requester", executor, supervisor.get(QueueId.TO_BE_REQUESTED),
        (UrlRequest urlRequest) -> {
          final Optional<?> requestValue = requestType(requester, urlRequest);

          if (requestValue.isPresent()) {
            urlRequest.future.complete(requestValue.get());
          } else {
            urlRequest.future.completeExceptionally(
                new RuntimeException(
                    "Future did not succeed, most likely because the response was a 404")
            );
          }
        }
    ).start(10);
  }

  private void initCrawler(
      ExecutorService executor,
      QueueSupervisor supervisor,
      Requester requester,
      Parser parser) {
    new UrlWorker<>("Crawler", executor, supervisor.get(QueueId.TO_BE_CRAWLED),
        (String urlToCrawl) -> {
          final String fixedUrl = Utilities.normalizeProtocol(urlToCrawl);

          if (!Utilities.isValidUrl(fixedUrl)) {
            LOG.info("Consumed URL is invalid - skipping: {}", fixedUrl);
            return;
          } else try {
            if (isBlacklisted(Utilities.getDomain(fixedUrl))) {
              LOG.info("URL is blacklisted - skipping: {}", fixedUrl);
              return;
            }
          } catch (URISyntaxException e) {
            LOG.error(String.format("Unable to parse url [%s]", fixedUrl), e);
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
        }
    ).start(10);
  }

  private void initAnalyzer(
      ExecutorService executor,
      QueueSupervisor supervisor,
      Requester requester,
      Parser parser) {
    new UrlWorker<>("Analyzer", executor, supervisor.get(QueueId.TO_BE_ANALYZED),
        (String urlToAnalyze) -> {
          final List<Plugin> plugins = Arrays.asList(
              new HtmlComments(requester, parser),
              new Wordpress(requester),
              new SubPageFinder(requester),
              new PhpInfo(requester)
          );

          final Analyzer analyzer = new Analyzer(plugins);
          final Set<Bug> bugs = analyzer.analyze(urlToAnalyze);

          bugs.forEach(bug -> supervisor.get(QueueId.TO_BE_STORED_AS_BUG).add(bug));
        }
    ).start(10);
  }

  private void initPersister(
      ExecutorService executor,
      QueueSupervisor supervisor,
      Persister persister) {
    new UrlWorker<>(
        "Persister",
        executor,
        supervisor.get(QueueId.TO_BE_STORED_AS_BUG),
        persister::storeBug
    ).start(10);
  }

  private Optional<?> requestType(Requester requester, UrlRequest request) {
    switch (request.type) {
      case RAW:
        return requester.request(request.url);
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
