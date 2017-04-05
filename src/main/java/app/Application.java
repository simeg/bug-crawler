package app;

import app.analyze.Analyzer;
import app.crawl.Crawler;
import com.google.common.collect.Queues;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication(scanBasePackages={"app"})
public class Application {

  private static final Logger LOG = Logger.getLogger(Application.class.getName());

  public static void main(String[] args) throws IOException, InterruptedException {
    final ExecutorService executor = Executors.newFixedThreadPool(2);
    final Application app = new Application();

    // QUESTION:
    // Is this a good approach?
    executor.submit(() -> {
      try {
        // TODO: Add logging
        app.start();
      } catch (InterruptedException e) {
        // TODO
        e.printStackTrace();
      }
    });

    executor.submit(() -> {
      // TODO: Add logging
      SpringApplication.run(app.getClass(), args);
    });
  }

  private void start() throws InterruptedException {
    /*
     * Init by creating three queues:
     * - subLinkQueue       (URLs to be crawled)
     * - crawledLinkQueue   (URLs to be analyzed)
     * - Bugs
     */

    // QUESTION
    // Why can't I use Queue class here?
    final LinkedBlockingQueue<String> subLinkQueue = Queues.newLinkedBlockingQueue();
    final LinkedBlockingQueue<String> crawledLinkQueue = Queues.newLinkedBlockingQueue();
    final LinkedBlockingQueue<String> bugsQueue = Queues.newLinkedBlockingQueue();

    subLinkQueue.add("http://www.vecka.nu");

    final ExecutorService producer = Executors.newFixedThreadPool(10);
    final ExecutorService consumer = Executors.newFixedThreadPool(10);

    while (true) {

      producer.submit(() -> {
        try {
          String urlToCrawl;
          if ((urlToCrawl = subLinkQueue.poll(10, TimeUnit.SECONDS)) != null) {

            LOG.log(Level.INFO, "Starting crawl thread with name: {0}", Thread.currentThread().getName());

            if (!isValidUrl(urlToCrawl)) {
              // TODO: Is this the best way to do it? Just return
              LOG.log(
                  Level.INFO,
                  "{0}: Consumed URL is invalid - aborting: {1}",
                  new Object[] {Thread.currentThread().getName(), urlToCrawl});
              return;
            }

            final Set<String> subLinks = new Crawler().getSubLinks(urlToCrawl);

            // URL is crawled and ready to be analyzed
            crawledLinkQueue.add(urlToCrawl);

            if (subLinks.size() > 0) {
              // Add sub-links back on URL queue
              subLinkQueue.addAll(subLinks);

              LOG.log(
                  Level.INFO,
                  "{0}: Found {1} sub-links: {2}",
                  new Object[] {Thread.currentThread().getName(), String.valueOf(subLinks.size()), subLinks});
            } else {
              LOG.log(
                  Level.INFO,
                  "{0}: No sub-links found for: {1}", new Object[] {Thread.currentThread().getName(), urlToCrawl});
            }
          }
        } catch (InterruptedException e) {
          LOG.log(
              Level.SEVERE,
              "{0}: Polling was interrupted: {1}",
              new Object[] {Thread.currentThread().getName(), e.toString()});
        }
      });

      consumer.submit(() -> {
        try {
          String urlToAnalyze;
          if ((urlToAnalyze = crawledLinkQueue.poll(10, TimeUnit.SECONDS)) != null) {
            LOG.log(Level.INFO, "Starting analyze thread with name: {0}", Thread.currentThread().getName());

            // Does nothing right now
            new Analyzer().analyze(urlToAnalyze);

          }
        } catch (InterruptedException e) {
          LOG.log(
              Level.SEVERE,
              "{0}: Polling was interrupted: {1}",
              new Object[] {Thread.currentThread().getName(), e.toString()});
        }
      });
    }

  }

  private boolean isValidUrl(String url) {
    // Should this really live here?
    // TODO
    return true;
  }
}
