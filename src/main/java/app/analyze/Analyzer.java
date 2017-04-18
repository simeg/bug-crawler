package app.analyze;

import app.analyze.Bug.BugType;
import app.parse.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Analyzer {
  /*
   * Shall detect security bugs for provided URL
   */

  private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

  private static final int RESULT_INITIAL_CAPACITY = 100000;

  private final Parser parser;
  private final List<Object> paths;

  public Analyzer(Parser parser, List<Object> paths) {
    this.parser = parser;
    this.paths = paths;
  }

  public Set<Bug> analyze(String url) {
    LOG.info("{}: Will analyze URL: {}", Thread.currentThread().getName(), url);
    final Set<Bug> result = new LinkedHashSet<>(RESULT_INITIAL_CAPACITY);

    result.addAll(getHtmlBugs(url));
    result.addAll(getFileBugs(url));

    return result;
  }

  private Set<Bug> getHtmlBugs(String url) {
    if (isWordpress(url)) {
      // TODO: Find wordpress bugs
    }
    return null;
  }

  boolean isWordpress(String url) {
    final String wpLoginPage = url + "/wp-login.php";
    try {
      return parser.getResponseStatusCode(wpLoginPage) == 200 && !isMatching(parser, url, wpLoginPage);
    } catch (IOException e) {
      LOG.error("{}: Error parsing URL: {}", Thread.currentThread().getName(), wpLoginPage);
    }

    return false;
  }

  Set<Bug> getFileBugs(String url) {
    final Set<Bug> result = new LinkedHashSet<>(RESULT_INITIAL_CAPACITY);

    this.paths.forEach(path -> {
      final String fullUrlPath = url + "/" + path;
      try {
        final int statusCode = parser.getResponseStatusCode(fullUrlPath);
        final boolean isMatching = isMatching(parser, url, fullUrlPath);

        if (statusCode == 200 && !isMatching) {
          LOG.info("{}: Found file {} on URL: {}", Thread.currentThread().getName(), path, url);
          result.add(
              Bug.create(
                  BugType.FILE_ACCESS,
                  url,
                  "Access to " + path,
                  Optional.of(fullUrlPath)
              )
          );
        }

      } catch (IOException e) {
        LOG.info("{}: Could not find file {} on URL: {}", Thread.currentThread().getName(), path, fullUrlPath);
      }
    });

    return result;
  }

  private boolean isMatching(Parser parser, String url, String fullUrlPath) {
    final int urlHash = parser.getHtmlHash(url);
    final int pathHash = parser.getHtmlHash(fullUrlPath);
    return urlHash == pathHash;
  }
}
