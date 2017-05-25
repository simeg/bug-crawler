package app.plugin;

import app.analyze.Bug;
import app.parse.Parser;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PageFinder implements Plugin {
  /*
   * Looks for pages known for being used for
   * sensitive access, configuration etc.
   *
   * If a page is found and it differs from
   * the root domain it's marked as a bug.
   */

  private static final Logger LOG = LoggerFactory.getLogger(PageFinder.class);

  private static final int FUTURE_TIMEOUT = 10;

  private final Requester requester;
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

  public PageFinder(Requester requester, Parser parser) {
    this.requester = requester;
    this.parser = parser;
  }

  @Override
  public Set<Bug> inspect(String url) {
    final Set<Bug> result = Sets.newHashSet();

    pagePaths.forEach(path -> {
      final String fullUrlPath = url + "/" + path;

      final CompletableFuture future = requester.get(url, UrlRequest.RequestType.STATUS_CODE);
      final int statusCode = getStatusCode(future);

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
    });

    return result;
  }

  private static int getStatusCode(CompletableFuture future) {
    while (!future.isDone()) {
      try {
        return (int) future.get(FUTURE_TIMEOUT, TimeUnit.SECONDS);

      } catch (InterruptedException e) {
        LOG.error("{}: Error when handling future. Thread was interrupted {}",
            Thread.currentThread().getName(), e.toString());
      } catch (ExecutionException e) {
        LOG.error("{}: Error when handling future. Future was completed exceptionally {}",
            Thread.currentThread().getName(), e.toString());
      } catch (TimeoutException e) {
        LOG.error("{}: Error when handling future. Future took too long time to finish {}",
            Thread.currentThread().getName(), e.toString());
      }
    }

    return -1;
  }

  private static boolean isMatching(Parser parser, String url, String fullUrlPath) {
    final int urlHash = parser.getHtmlHash(url);
    final int pathHash = parser.getHtmlHash(fullUrlPath);
    return urlHash == pathHash;
  }

}
