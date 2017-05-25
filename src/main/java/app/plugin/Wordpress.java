package app.plugin;

import app.analyze.Bug;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Wordpress implements Plugin {

  private static final Logger LOG = LoggerFactory.getLogger(Wordpress.class);

  private static final int FUTURE_TIMEOUT = 10;

  private final Requester requester;

  public Wordpress(Requester requester) {
    this.requester = requester;
  }

  @Override
  public String getDescription() {
    return
        "Handles all Wordpress related bugs";
  }

  @Override
  public Set<Bug> inspect(String url) {
    if (isWordpress(url)) {
      final Set<Bug> result = Sets.newHashSet();
      final int wpVersion = getWpVersion(url);

      // TODO: Find wordpress bugs
    } else {
      LOG.info(
          "{}: Website is not a Wordpress instance, will not look for Wordpress bugs",
          Thread.currentThread().getName()
      );
    }

    return Collections.emptySet();
  }

  private int getWpVersion(String url) {
    // TODO: https://github.com/andresriancho/w3af/blob/master/w3af/plugins/crawl/wordpress_fingerprint.py#L84
    return -1;
  }

  boolean isWordpress(String url) {
    final String wpLoginUrl = url + "/wp-login.php";

    final CompletableFuture future = requester.get(wpLoginUrl, UrlRequest.RequestType.STATUS_CODE);
    final int statusCode = getStatusCode(future);

    final boolean isWordpressInstance = (statusCode == 200 && !isMatching(url, wpLoginUrl));
    return isWordpressInstance;
  }

  private boolean isMatching(String baseUrl, String otherUrl) {
    final CompletableFuture baseUrlFuture = requester.get(baseUrl, UrlRequest.RequestType.HTML_HASH);
    final CompletableFuture otherUrlFuture = requester.get(otherUrl, UrlRequest.RequestType.HTML_HASH);
    final String baseUrlHtml = getHtmlHash(baseUrlFuture);
    final String otherUrlHtml = getHtmlHash(otherUrlFuture);
    return baseUrlHtml.equals(otherUrlHtml);
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
