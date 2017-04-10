package app.analyze;

import app.parse.Parser;
import com.google.common.collect.Sets;
import org.jsoup.Connection.Response;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class Analyzer {
  /*
   * Shall detect security bugs for provided URL
   */

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Analyzer.class);

  private final Parser parser;

  public Analyzer(Parser parser) {
    this.parser = parser;
  }

  public Set<Bug> analyze(String url) {
    LOG.info("{}: Will analyze URL: {}", Thread.currentThread().getName(), url);
    final Set<Bug> result = Sets.newHashSet();

    result.addAll(getFileBugs(url));

    return result;
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
        Response response = this.parser.getResponse(url + path);

        // QUESTION:
        // Is this a valid way to check?
        if (response.statusCode() == 200) {
          LOG.info("{}: Found file {} on URL: {}", Thread.currentThread().getName(), path, url);
          result.add(new Bug(url, "Access to " + path, Optional.of(url + path)));
        }

      } catch (IOException e) {
        LOG.error("{}: Could not find file {} on URL: {}", Thread.currentThread().getName(), path, url + path);
      }
    });

    return result;
  }
}
