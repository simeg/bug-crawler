package app.util;

import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.Sets;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Utilities {

  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

  private static final int FUTURE_TIMEOUT = 10;

  public static boolean isValidUrl(String url) {
    final String[] validSchemas = {"http", "https"};
    return new UrlValidator(validSchemas, 2L).isValid(url);
  }

  public static String getDomain(String url) {
    // http://stackoverflow.com/questions/9607903/get-domain-name-from-given-url
    try {
      final String domain = new URI(url).getHost();
      return domain.startsWith("www.") ? domain.substring(4) : domain;
    } catch (URISyntaxException e) {
      // TODO: Propagate exception, do not return null
      LOG.warn("Unable to parse URL: [{}]", url);
    }

    return null;
  }

  public static String normalizeProtocol(String url) {
    // https://jsoup.org/cookbook/extracting-data/working-with-urls

    /*
     * TODO:
     * - Fix anchor links
    */

    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }

    // TODO: Consider using regexp for this
    if (url.startsWith("/")) {
      return "http:/" + url;
    }

    return "http://" + url;
  }

  public static boolean isBlacklisted(String domain) {
    // TODO: Add github to list when vecka.nu is not used as
    // test website anymore
    return Sets.newHashSet(
        "google.com",
        "youtube.com",
        "facebook.com",
        "baidu.com",
        "wikipedia.org",
        "yahoo.com",
        "reddit.com",
        "amazon.com",
        "twitter.com",
        "instagram.com",
        "linkedin.com"
    ).contains(domain);
  }

  public static Object getFutureResult(CompletableFuture future) {
    while (!future.isDone()) {
      try {
        return future.get(FUTURE_TIMEOUT, TimeUnit.SECONDS);

      } catch (InterruptedException e) {
        LOG.error("Thread was interrupted {}", e.toString());
      } catch (ExecutionException e) {
        LOG.error("Future was interrupted {}", e.toString());
      } catch (TimeoutException e) {
        LOG.error("Future took too long time to finish {}", e.toString());
      }
    }

    return null;
  }

  public static boolean isMatching(Requester requester, String baseUrl, String otherUrl) {
    final CompletableFuture baseUrlFuture = requester.get(baseUrl, UrlRequest.RequestType.HTML_HASH);
    final CompletableFuture otherUrlFuture = requester.get(otherUrl, UrlRequest.RequestType.HTML_HASH);
    final String baseUrlHtml = String.valueOf(getFutureResult(baseUrlFuture));
    final String otherUrlHtml = String.valueOf(getFutureResult(otherUrlFuture));
    return baseUrlHtml.equals(otherUrlHtml);
  }

}
