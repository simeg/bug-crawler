package app;

import app.crawl.Crawler;
import app.queue.Queue;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication(scanBasePackages={"app"})
public class Application {

  private static final Logger LOG = Logger.getLogger(Application.class.getName());

  public static void main(String[] args) throws IOException {
    /*
     * Init by creating three queues:
     * - subLinkQueue       (URLs to be crawled)
     * - crawledLinkQueue   (URLs to be analyzed)
     * - Bugs
     */

    final Queue subLinkQueue = new Queue();
    final Queue crawledLinkQueue = new Queue();

    subLinkQueue.add("http://www.vecka.nu");

    // QUESTION: How to control this? Concurrency issue? Prove problem by using debugger over this block
//    while (subLinkQueue.peek() != null) {

    new Thread(() -> {
      LOG.log(Level.INFO, "Starting thread with name: {0}", Thread.currentThread().getName());

      // Crawler should add sub-links to subLinkQueue?
      // And put current URL on crawledLinkQueue
      Crawler crawler = new Crawler();
      String url = subLinkQueue.remove().toString();
      Set<String> subLinks = crawler.getSubLinks(url);
      
    }).start();

//    }

//    Application app = new Application();
//    app.start();
//    SpringApplication.run(app.getClass(), args);
  }
}
