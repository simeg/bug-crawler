package app;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.crawl.Crawler;
import app.crawl.InvalidExtensionException;
import app.parse.HtmlParser;
import app.parse.Parser;
import app.persist.Persister;
import app.plugin.*;
import app.queue.QueueId;
import app.queue.QueueSupervisor;
import app.request.JsoupRequester;
import app.request.Requester;
import app.request.UrlRequest;
import app.work.UrlWorker;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.mola.galimatias.GalimatiasParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static app.util.RequestUtils.requestType;
import static app.util.UrlUtils.validateUrl;
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

    final Requester requester =
        new JsoupRequester(supervisor.get(QueueId.TO_BE_REQUESTED), Maps.newHashMap());

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
    // TODO: This URL should be validated separately since it's coming from a user,
    //   all other URLs should be validated inside the Crawler so everything that comes out
    //   of the Crawler should be validated and good!
    supervisor.get(QueueId.TO_BE_CRAWLED).add(url);

    initRequester(executor, supervisor, requester);
    initCrawler(executor, supervisor, requester, parser);
    initAnalyzer(executor, supervisor, requester, parser);
    initPersister(executor, supervisor, persister);

    logStartSuccess();
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
          try {
            final String url = validateUrl(urlToCrawl);

            if (isBlacklisted(url)) {
              LOG.info("URL is blacklisted - skipping: {}", url.substring(0, 30) + "...");
              return;
            }

            final Set<String> subLinks = new Crawler(requester, parser).getSubLinks(url);

            // URL is crawled and ready to be analyzed
            supervisor.get(QueueId.TO_BE_ANALYZED).add(url);

            if (subLinks.size() > 0) {
              // Add sub-links back on URL queue
              subLinks.forEach(link -> supervisor.get(QueueId.TO_BE_CRAWLED).add(link));

              LOG.info("Found {} sub-links for: {}", String.valueOf(subLinks.size()), url);
            } else {
              LOG.info("No sub-links found for: {}", url);
            }

          } catch (InvalidExtensionException | GalimatiasParseException e) {
            LOG.error(String.format("Unable to parse url [%s]", urlToCrawl), e);
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
          final ImmutableSet<Bug> bugs = analyzer.analyze(urlToAnalyze);

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

  private Persister getPersister(Config conf) {
    return Persister.create(
        "org.postgresql.Driver",
        conf.getString("db.host"),
        conf.getInt("db.port"),
        conf.getString("db.name"),
        conf.getString("db.username"),
        conf.getString("db.password"));
  }

  private void logStartSuccess() {
    LOG.info("\n##############################################"
        + "\n###### Application successfully started ######"
        + "\n##############################################");
  }

}
