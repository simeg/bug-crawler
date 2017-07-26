package app;

import app.crawl.InvalidExtensionException;
import app.parse.HtmlParser;
import app.parse.Parser;
import app.persist.Persister;
import app.queue.QueueId;
import app.queue.QueueSupervisor;
import app.request.JsoupRequester;
import app.request.Requester;
import app.url.Url;
import app.url.UrlParseException;
import app.work.AnalyzerWorker;
import app.work.CrawlerWorker;
import app.work.PersisterWorker;
import app.work.RequesterWorker;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

  void start(
      String rawUrl,
      QueueSupervisor supervisor,
      ExecutorService executor,
      Parser parser,
      Requester requester,
      Persister persister) {
    try {
      Url url = new Url(rawUrl);
      supervisor.get(QueueId.TO_BE_CRAWLED).add(url.rawUrl); // TODO: Use Url class

      initWorkers(executor, requester, parser, supervisor, persister);

      logStartSuccess();
    } catch (InvalidExtensionException | UrlParseException e) {
      isRunning = false;
      LOG.error("Could not start application due to invalid initial URL={}", rawUrl, e);
    }
  }

  private void initWorkers(
      ExecutorService executor,
      Requester requester,
      Parser parser,
      QueueSupervisor supervisor,
      Persister persister) {
    new RequesterWorker(executor, requester, supervisor.get(QueueId.TO_BE_REQUESTED))
        .start(10);

    new CrawlerWorker(
        executor,
        requester,
        parser,
        supervisor,
        supervisor.get(QueueId.TO_BE_CRAWLED)
    ).start(10);

    new AnalyzerWorker(
        executor,
        requester,
        parser,
        supervisor,
        supervisor.get(QueueId.TO_BE_ANALYZED)
    ).start(10);

    new PersisterWorker(executor, persister, supervisor.get(QueueId.TO_BE_STORED_AS_BUG))
        .start(10);
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
