package app.analyze;

import app.queue.PersistentQueue;
import com.google.common.collect.Sets;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class Analyzer {
  /*
   * Shall detect security bugs for provided URL
   */

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Analyzer.class);

  private final PersistentQueue queue;

  private Analyzer(PersistentQueue queue) {
    this.queue = queue;
  }

  public static Analyzer create(PersistentQueue queue) {
    return new Analyzer(queue);
  }

  public Set<Bug> analyze(String url) {
    LOG.info("{}: Will analyze URL: {}", Thread.currentThread().getName(), url);
    final Set<Bug> result = Sets.newHashSet();

    Document doc = parseHtml(url);

    result.addAll(getFileBugs(url));

    return result;
  }

  private static Document parseHtml(String url) {
    try {
      return Jsoup.connect(url).get();
    } catch (IOException e) {
      LOG.error("{}: Unable to parse the URL: {}", Thread.currentThread().getName(), e.toString());
      return null;
    }
  }

  private Set<Bug> getFileBugs(String url) {
    // TODO: Store these elsewhere?
    final Set<Bug> result = Sets.newHashSet();
    final Set<String> paths = Sets.newHashSet();
    paths.add("/phpinfo.php");
    paths.add("/phpmyadmin");
    paths.add("/.htaccess");
    paths.add("/.htaccess.bak");
    paths.add("/.htpasswd");
    paths.add("/.htpasswd.bak");

    paths.forEach(path -> {
      try {
        Response response = Jsoup.connect(url + path)
            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
            .timeout(10000)
            .execute();

        // QUESTION:
        // Is this a valid way to check?
        if (response.statusCode() == 200) {
          LOG.info("{}: Found file {} on URL: {}", Thread.currentThread().getName(), path, url);
          result.add(new Bug(url, "Access to " + path, Optional.of(url + path)));
        }

      } catch (IOException e) {
        LOG.error("{}: Could not find file on URL: {}", Thread.currentThread().getName(), url + path);
      }
    });

    return result;
  }
}
