package app.request;

import app.queue.PersistentQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class JsoupRequester implements Requester {

  private static final Logger LOG = LoggerFactory.getLogger(JsoupRequester.class);

  private static final int TIMEOUT = 10000;
  private static final String USER_AGENT =
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 " +
          "(KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

  private final PersistentQueue<UrlRequest> queue;
  private final HashMap<String, Document> cache;

  public JsoupRequester(PersistentQueue queue, HashMap<String, Document> requestCache) {
    this.queue = queue;
    this.cache = requestCache;
  }

  @Override
  public CompletableFuture get(String url, UrlRequest.RequestType type) {
    final CompletableFuture future = new CompletableFuture();
    queue.add(new UrlRequest(url, future, type));

    return future;
  }

  @Override
  public Object requestHtml(String url) {
    if (cache.containsKey(url)) {
      return cache.get(url);
    }

    final Document result = this.makeRequest(url);
    cache.put(url, result);

    return result;
  }

  @Override
  public Object requestHtmlHash(String url) {
    if (cache.containsKey(url)) {
      return cache.get(url).body().html().hashCode();
    }

    final Document result = this.makeRequest(url);

    if (result != null) {
      cache.put(url, result);
      // Only hash the content of the <body> element
      return result.body().html().hashCode();
    }

    return null;
  }

  private Document makeRequest(String url) {
    try {
      return Jsoup.connect(url)
          .timeout(TIMEOUT)
          .userAgent(USER_AGENT)
          .get();
    } catch (IOException e) {
      LOG.warn("Unable to get requested URL=[{}] with error=[{}]", url, e.toString());
    }

    return null;
  }

  @Override
  public int requestStatusCode(String url) {
    try {
      return Jsoup.connect(url)
          .timeout(TIMEOUT)
          .userAgent(USER_AGENT)
          .execute()
          .statusCode();
    } catch (IOException e) {
      LOG.warn("Unable to get requested URL=[{}] with error={}", url, e.toString());
    }

    return -1;
  }
}
