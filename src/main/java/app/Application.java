package app;

import app.analyze.Analyzer;
import app.crawl.Crawler;
import app.queue.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication(scanBasePackages={"app"})
public class Application {

  private static final Logger LOG = Logger.getLogger(Application.class.getName());

  public static void main(String[] args) throws IOException, InterruptedException {
    final Application app = new Application();
    app.start();
    SpringApplication.run(app.getClass(), args);
  }

  private void start() throws InterruptedException {
    /*
     * Init by creating three queues:
     * - subLinkQueue       (URLs to be crawled)
     * - crawledLinkQueue   (URLs to be analyzed)
     * - Bugs
     */

    final Queue subLinkQueue = new Queue();
    final Queue crawledLinkQueue = new Queue();
    final Queue bugsQueue = new Queue();

    subLinkQueue.add("http://www.vecka.nu");

    /*
     * TODO
     * - Verify that URL consumed from subLinkQueue is sane
     */
    while (true) {

      if (subLinkQueue.peek() != null) {
        // QUESTION: If sleep is removed, it will go "out of sync" and try to remove() from
        // queue even though there's nothing on there.
        Thread.sleep(1000);

        new Thread(() -> {
          LOG.log(Level.INFO, "Starting crawl thread with name: {0}", Thread.currentThread().getName());

          String urlToCrawl = null;
          urlToCrawl = subLinkQueue.remove();

          /*try {
          } catch (NoSuchElementException e) {
            LOG.log(Level.INFO, "Ignore exception: {0}", e.toString());
          }*/

          if (urlToCrawl != null) {
            final Set<String> subLinks = new Crawler().getSubLinks(urlToCrawl);

            // Add sub-links on to queue.
            // QUESTION: Should it really do this?
            subLinkQueue.addAll(subLinks);

            // URL is crawled and ready to be analyzed
            crawledLinkQueue.add(urlToCrawl);

            System.out.println(subLinks);
            System.out.println(subLinks.size());
          }
        }).start();
      }

      if (crawledLinkQueue.peek() != null) {
        // QUESTION: If sleep is removed, it will go "out of sync" and try to remove() from
        // queue even though there's nothing on there.
        Thread.sleep(1000);

        new Thread(() -> {
          LOG.log(Level.INFO, "Starting analyze thread with name: {0}", Thread.currentThread().getName());

          String urlToAnalyze = null;
          urlToAnalyze = crawledLinkQueue.remove();

          /*try {
          } catch (NoSuchElementException e) {
            LOG.log(Level.INFO, "Ignore exception: {0}", e.toString());
          }*/

          // Does nothing right now
          if (urlToAnalyze != null)
            new Analyzer().analyze(urlToAnalyze);

        }).start();
      }
    }

  }
}
