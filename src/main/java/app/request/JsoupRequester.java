package app.request;

import app.queue.SimpleQueue;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class JsoupRequester implements Requester {

  private static final Logger LOG = LoggerFactory.getLogger(JsoupRequester.class);

  private static final int TIMEOUT_MS = 3000;
  private static final String USER_AGENT =
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 " +
          "(KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

  private final SimpleQueue<UrlRequest> queue;
  private final HashMap<String, Document> cache;

  public JsoupRequester(SimpleQueue<UrlRequest> queue, HashMap<String, Document> requestCache) {
    this.queue = queue;
    this.cache = requestCache;
  }

  @Override
  public CompletableFuture init(String url, UrlRequest.RequestType type) {
    final CompletableFuture future = new CompletableFuture();
    queue.add(new UrlRequest(url, future, type));

    return future;
  }

  @Override
  public Optional<Document> requestHtml(String url) {
    if (cache.containsKey(url)) {
      return Optional.of(cache.get(url));
    }

    final Optional<Document> result = this.makeRequest(url);
    result.ifPresent(html -> cache.put(url, html));

    return result;
  }

  @Override
  public Optional<Integer> requestHtmlHashCode(String url) {
    if (cache.containsKey(url)) {
      return Optional.of(cache.get(url).body().html().hashCode());
    }

    final Optional<Document> result = this.makeRequest(url);
    if (result.isPresent()) {
      cache.put(url, result.get());

      // Only hash the content of the <body> element
      return Optional.of(result.get().body().html().hashCode());
    }

    return Optional.empty();
  }

  @Override
  public Optional<Integer> requestStatusCode(String url) {
    try {
      return Optional.of(
          Jsoup.connect(url)
              .timeout(TIMEOUT_MS)
              .userAgent(USER_AGENT)
              .execute()
              .statusCode());

    } catch (HttpStatusException e) {
      // If status code != 200
      return Optional.of(e.getStatusCode());

    } catch (IOException e) {
      return Optional.of(404);

    } catch (Throwable e) {
      throw new RuntimeException(String.format("Unable to get requested URL=[%s]", url), e);
    }
  }

  private Optional<Document> makeRequest(String url) {
    try {
      return Optional.of(
          Jsoup.connect(url)
              .timeout(TIMEOUT_MS)
              .userAgent(USER_AGENT)
              .get());

    } catch (IllegalArgumentException e) {
      LOG.error("Malformed URL: {}", url);
      return Optional.empty();

    } catch (IOException e) {
      return Optional.empty();

    } catch (Throwable e) {
      throw new RuntimeException(String.format("Unable to get requested URL=[%s]", url), e);
    }
  }
}
