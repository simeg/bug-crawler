package app.plugin;

import app.analyze.Bug;
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

  public PageFinder(Requester requester) {
    this.requester = requester;
  }

  @Override
  public Set<Bug> inspect(String url) {
    final Set<Bug> result = Sets.newHashSet();

    pagePaths.forEach(path -> {
      final String fullUrlPath = url + "/" + path;

      final CompletableFuture future = requester.get(url, UrlRequest.RequestType.STATUS_CODE);
      final int statusCode = getStatusCode(future);

      // If the url + path has exactly the same HTML content as the url,
      // it's a false-positive and should not be reported as a potential bug.
      // Therefore we're checking here to see if they are matching.
      final boolean isMatching = isMatching(url, fullUrlPath);

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

  private boolean isMatching(String baseUrl, String fullUrlPath) {
    final CompletableFuture baseUrlFuture = requester.get(baseUrl, UrlRequest.RequestType.HTML);
    final CompletableFuture pathUrlFuture = requester.get(fullUrlPath, UrlRequest.RequestType.HTML);
    final String baseUrlHtml = getHtmlHash(baseUrlFuture);
    final String pathUrlHtml = getHtmlHash(pathUrlFuture);
    return baseUrlHtml.equals(pathUrlHtml);
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

  private static String getHtmlHash(CompletableFuture future) {
    while (!future.isDone()) {
      try {
        return String.valueOf(future.get(FUTURE_TIMEOUT, TimeUnit.SECONDS));

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

    return null;
  }

}
