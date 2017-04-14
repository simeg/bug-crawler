package app.analyze;

import app.analyze.Bug.BugType;
import app.parse.Parser;
import com.google.common.collect.Sets;
import org.jsoup.Connection.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Analyzer {
  /*
   * Shall detect security bugs for provided URL
   */

  private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

  private final Parser parser;
  private final List<Object> paths;

  public Analyzer(Parser parser, List<Object> paths) {
    this.parser = parser;
    this.paths = paths;
  }

  public Set<Bug> analyze(String url) {
    LOG.info("{}: Will analyze URL: {}", Thread.currentThread().getName(), url);
    final Set<Bug> result = Sets.newHashSet();

    result.addAll(getFileBugs(url));

    return result;
  }

  private Set<Bug> getFileBugs(String url) {
    final Set<Bug> result = Sets.newHashSet();

    this.paths.forEach(path -> {
      try {
        Response response = this.parser.getResponse(url + path);

        // QUESTION:
        // Is this a valid way to check?
        if (response.statusCode() == 200) {
          LOG.info("{}: Found file {} on URL: {}", Thread.currentThread().getName(), path, url);
          result.add(
              Bug.create(
                  BugType.FILE_ACCESS,
                  url,
                  "Access to " + path,
                  Optional.of(url + path)
              )
          );
        }

      } catch (IOException e) {
        LOG.error("{}: Could not find file {} on URL: {}", Thread.currentThread().getName(), path, url + path);
      }
    });

    return result;
  }
}
