package app.analyze;

import app.analyze.Bug.BugType;
import app.parse.Parser;
import app.plugin.Plugin;
import com.google.common.collect.Sets;
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
  private final List<Plugin> plugins;

  private Analyzer(Parser parser, List<Object> paths, List<Plugin> plugins) {
    this.parser = parser;
    this.paths = paths;
    this.plugins = plugins;
  }

  public static Analyzer create(Parser parser, List<Object> paths, List<Plugin> plugins) {
    return new Analyzer(parser, paths, plugins);
  }

  public Set<Bug> analyze(String url) {
    LOG.info("{}: Will analyze URL: {}", Thread.currentThread().getName(), url);
    final Set<Bug> result = Sets.newHashSet();

    plugins.forEach((plugin ->
        result.addAll(plugin.inspect(url))
    ));

    // TODO: Make a plugin class for finding file bugs
    result.addAll(findFileBugs(url));

    return result;
  }

  Set<Bug> findFileBugs(String url) {
    final Set<Bug> result = Sets.newHashSet();

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
