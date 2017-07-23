package app.util;

import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestUtils {

  private static final Logger LOG = LoggerFactory.getLogger(RequestUtils.class);

  private static final int FUTURE_TIMEOUT_SEC = 15;

  public static Optional<?> requestType(Requester requester, UrlRequest request) {
    switch (request.type) {
      case RAW:
        return requester.request(request.url);
      case HTML:
        return requester.requestHtml(request.url);
      case HTML_HASH:
        return requester.requestHtmlHashCode(request.url);
      case STATUS_CODE:
        return requester.requestStatusCode(request.url);
      default:
        LOG.warn("Unknown request type=[{}]", request.type);
        break;
    }

    return Optional.empty();
  }

  public static Object getFutureResult(CompletableFuture future) throws BadFutureException {
    while (!future.isDone()) {
      try {
        return future.get(FUTURE_TIMEOUT_SEC, TimeUnit.SECONDS);

      } catch (InterruptedException e) {
        throw new BadFutureException("Thread was interrupted", e);
      } catch (ExecutionException e) {
        throw new BadFutureException(
            "Future was interrupted. A bad response may have caused this.", e);
      } catch (TimeoutException e) {
        throw new BadFutureException("Future took too long to finish", e);
      }
    }

    throw new BadFutureException(
        String.format("I'm not sure how this happened,"
            + " future is done=[%s] and future is cancelled=[%s]",
            String.valueOf(future.isDone()),
            String.valueOf(future.isCancelled())));
  }

  // TODO: This should be called isDifferent since that's how it's used everywhere
  // TODO: It should not compare to the base URL, it should compare with a random sub-site
  // of the base URL. Just like how you can consider the sites we're checking with in SubPageFinder
  // for example. And if /phpmyadmin and /whatever is different then it might be a potential bug.
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

}
