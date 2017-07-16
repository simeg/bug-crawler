package app.util;

import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.Sets;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Utilities {

  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

  private static final int FUTURE_TIMEOUT_SEC = 15;

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
      // TODO: Return empty optional? Thread should continue even if parsing of url fails
      throw new RuntimeException(String.format("Unable to parse url [%s]", url), e);
    }
  }

  public static String normalizeProtocol(String url) {
    final String lowercaseUrl = url.toLowerCase();

    // All is good
    if (lowercaseUrl.startsWith("http://") || lowercaseUrl.startsWith("https://")) {
      return lowercaseUrl;
    }

    if (lowercaseUrl.startsWith("/")) {
      return "http:/" + lowercaseUrl;
    }

    return "http://" + lowercaseUrl;
  }

  public static boolean isBlacklisted(String domain) {
    return Sets.newHashSet(
        "localhost",
        "127.0.0.1",
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
        "linkedin.com",
        "github.com"
    ).contains(domain);
  }

  public static Object getFutureResult(CompletableFuture future) throws BadFutureException {
    while (!future.isDone()) {
      try {
        return future.get(FUTURE_TIMEOUT_SEC, TimeUnit.SECONDS);

      } catch (InterruptedException e) {
        throw new BadFutureException("Thread was interrupted", e);
      } catch (ExecutionException e) {
        throw new BadFutureException("Future was interrupted. A bad response may have caused this.", e);
      } catch (TimeoutException e) {
        throw new BadFutureException("Future took too long to finish", e);
      }
    }

    return null;
  }

  public static boolean isMatching(Requester requester, String baseUrl, String otherUrl) {
    try {
      final CompletableFuture baseUrlFuture =
          requester.init(baseUrl, UrlRequest.RequestType.HTML_HASH);
      final CompletableFuture otherUrlFuture =
          requester.init(otherUrl, UrlRequest.RequestType.HTML_HASH);
      final String baseUrlHtml = String.valueOf(getFutureResult(baseUrlFuture));
      final String otherUrlHtml = String.valueOf(getFutureResult(otherUrlFuture));
      return baseUrlHtml.equals(otherUrlHtml);

    } catch (BadFutureException e) {
      return false;
    }
  }

  public static Optional<Document> parse(Connection.Response response) {
    try {
      return Optional.of(response.parse());

    } catch (IOException e) {
      LOG.warn("Unable to parse response from URL: [{}]", response.url());
    }

    return Optional.empty();
  }

}
