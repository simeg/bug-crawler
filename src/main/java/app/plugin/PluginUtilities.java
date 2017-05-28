package app.plugin;

import app.request.Requester;
import app.request.UrlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PluginUtilities {

  private static final Logger LOG = LoggerFactory.getLogger(PluginUtilities.class);

  private static final int FUTURE_TIMEOUT = 10;

  public static boolean isMatching(Requester requester, String baseUrl, String otherUrl) {
    final CompletableFuture baseUrlFuture = requester.get(baseUrl, UrlRequest.RequestType.HTML_HASH);
    final CompletableFuture otherUrlFuture = requester.get(otherUrl, UrlRequest.RequestType.HTML_HASH);
    final String baseUrlHtml = String.valueOf(getFutureResult(baseUrlFuture));
    final String otherUrlHtml = String.valueOf(getFutureResult(otherUrlFuture));
    return baseUrlHtml.equals(otherUrlHtml);
  }

  static Object getFutureResult(CompletableFuture future) {
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
}
