package app.plugin;

import app.analyze.Bug;
import app.parse.Parser;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PageFinder implements Plugin<Bug> {
  /*
   * Looks for pages known for being used for
   * sensitive access, configuration etc.
   *
   * If a page is found and it differs from
   * the root domain it's marked as a bug.
   */

  private static final Logger LOG = LoggerFactory.getLogger(PageFinder.class);

  private final Parser parser;
  private final List<String> pagePaths =
      Arrays.asList(
          "phpinfo.php",
          "phpmyadmin",
          "test.php?mode=phpinfo",
          ".htaccess",
          ".htaccess.bak",
          ".htpasswd",
          ".htpasswd.bak"
      );

  public PageFinder(Parser parser) {
    this.parser = parser;
  }

  @Override
  public Set<Bug> inspect(String url) {
    final Set<Bug> result = Sets.newHashSet();

    pagePaths.forEach(path -> {
      final String fullUrlPath = url + "/" + path;
      try {
        // TODO: Use queue for making requests - do not do it "on the side"
        final int statusCode = parser.getResponseStatusCode(fullUrlPath);
        final boolean isMatching = isMatching(parser, url, fullUrlPath);

        if (statusCode == 200 && !isMatching) {
          LOG.info("{}: Found file {} on URL: {}", Thread.currentThread().getName(), path, url);
          result.add(
              Bug.create(
                  Bug.BugType.FILE_ACCESS,
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

  private static boolean isMatching(Parser parser, String url, String fullUrlPath) {
    final int urlHash = parser.getHtmlHash(url);
    final int pathHash = parser.getHtmlHash(fullUrlPath);
    return urlHash == pathHash;
  }

}
