package app;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.crawl.Crawler;
import app.persist.Persister;
import app.queue.PersistentQueue;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
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

    app.start();
//    SpringApplication.run(app.getClass(), args);
  }

  private void start() throws InterruptedException {
    /*
     * Init by creating three queues:
     * - subLinkQueue       (URLs to be crawled)
     * - crawledLinkQueue   (URLs to be analyzed)
     * - Bugs
     */

    final Persister persister = Persister.create();

    final PersistentQueue<String> subLinkQueue = PersistentQueue.create(Queues.newLinkedBlockingQueue(), persister);
    final PersistentQueue<String> crawledLinkQueue = PersistentQueue.create(Queues.newLinkedBlockingQueue(), persister);
    final PersistentQueue<Bug> bugsQueue = PersistentQueue.create(Queues.newLinkedBlockingQueue(), persister);

    subLinkQueue.add("http://www.vecka.nu");

    final ExecutorService executor = Executors.newFixedThreadPool(50);

    submitWorkerNTimes(10, executor, subLinkQueue, urlToCrawl -> {
      LOG.info("Starting crawl thread with name: {}", Thread.currentThread().getName());

      if (!isValidUrl(urlToCrawl)) {
        LOG.info(
            "{}: Consumed URL is invalid - aborting: {}",
            Thread.currentThread().getName(), urlToCrawl);
        return;
      }

      final Set<String> subLinks = new Crawler().getSubLinks(urlToCrawl);

      // URL is crawled and ready to be analyzed
      crawledLinkQueue.add(urlToCrawl);

      if (subLinks.size() > 0) {
        // Add sub-links back on URL queue
        subLinkQueue.addAll(subLinks);

        LOG.info(
            "{}: Found {} sub-links: {}",
            Thread.currentThread().getName(), String.valueOf(subLinks.size()), subLinks);
      } else {
        LOG.info(
            "{}: No sub-links found for: {}",
            Thread.currentThread().getName(), urlToCrawl);
      }
    });

    submitWorkerNTimes(10, executor, crawledLinkQueue, urlToAnalyze -> {
      if (urlToAnalyze != null) {
        LOG.info("Starting analyze thread with name: {}", Thread.currentThread().getName());

        final Analyzer analyzer = new Analyzer();
        final Set<Bug> bugs = analyzer.analyze(urlToAnalyze);

        bugsQueue.addAll(bugs);
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
            T urlToAnalyze = queue.poll(10, TimeUnit.SECONDS);

            jobToDo.accept(urlToAnalyze);

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

  private boolean isValidUrl(String url) {
    // Should this really live here?
    // TODO
    return true;
  }
}
