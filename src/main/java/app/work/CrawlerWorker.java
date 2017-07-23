package app.work;

import app.crawl.Crawler;
import app.crawl.InvalidExtensionException;
import app.parse.Parser;
import app.queue.QueueId;
import app.queue.QueueSupervisor;
import app.queue.SimpleQueue;
import app.request.Requester;
import io.mola.galimatias.GalimatiasParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import static app.util.UrlUtils.validateUrl;
import static app.util.Utilities.isBlacklisted;
import static app.work.QueuePoller.pollQueue;

public class CrawlerWorker implements Worker<String> {

  private static final Logger LOG = LoggerFactory.getLogger(CrawlerWorker.class);

  private final ExecutorService executor;
  private final Requester requester;
  private final Parser parser;
  private final QueueSupervisor supervisor;
  private final SimpleQueue<String> queue;

  public CrawlerWorker(
      ExecutorService executor,
      Requester requester,
      Parser parser,
      QueueSupervisor supervisor,
      SimpleQueue<String> queue) {
    this.executor = executor;
    this.requester = requester;
    this.parser = parser;
    this.supervisor = supervisor;
    this.queue = queue;
  }

  @Override
  public void start(int threadCount) {
    IntStream.range(0, threadCount).forEach(i ->
        pollQueue("Crawler", i, LOG, executor, queue, this::crawl)
    );
  }

  private void crawl(String urlToCrawl) {
    try {
      final String url = validateUrl(urlToCrawl);

      if (isBlacklisted(url)) {
        String logUrl = (url.length() > 30 ? url.substring(0, 30) : url);
        LOG.info("URL is blacklisted - skipping: {}", logUrl + "...");
        return;
      }

      Set<String> subLinks = new Crawler(requester, parser).getSubLinks(url);

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

}
