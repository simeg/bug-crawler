package app.request;

import app.queue.SimpleQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
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
    cache.put(url, result);

    // Only hash the content of the <body> element
    return result.body().html().hashCode();
  }

  private Document makeRequest(String url) {
    try {
      LOG.info("Requester START: " + url);
      final Document document = Jsoup.connect(url)
          .timeout(TIMEOUT_MS)
          .userAgent(USER_AGENT)
          .get();
      LOG.info("Requester OK: " + url);

      return document;
    } catch (IOException e) {
      LOG.info("FAILED {}", url, e);
      throw new RuntimeException(String.format("Unable to get requested URL=[%s]", url), e);
    } catch (Throwable e) {
      LOG.info("Oh god", e);
      throw e;
    }
  }

  @Override
  public int requestStatusCode(String url) {
    try {
      return Jsoup.connect(url)
          .timeout(TIMEOUT_MS)
          .userAgent(USER_AGENT)
          .execute()
          .statusCode();
    } catch (IOException e) {
      throw new RuntimeException(String.format("Unable to get requested URL=[%s]", url), e);
    }
  }
}
