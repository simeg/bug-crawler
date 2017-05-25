package app.request;

import app.queue.PersistentQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Requester {

  private static final Logger LOG = LoggerFactory.getLogger(Requester.class);

  private static final int TIMEOUT = 10000;

  private final PersistentQueue queue;
  private final HashMap<String, Object> cache;

  public Requester(PersistentQueue queue, HashMap<String, Object> requestCache) {
    this.queue = queue;
    this.cache = requestCache;
  }

  public CompletableFuture get(String url) {
    final CompletableFuture future = new CompletableFuture();
    queue.add(new UrlRequest(url, future));

    return future;
  }

  public Object request(String url) {
    if (cache.containsKey(url)) {
      return cache.get(url);
    }

    final Document result = this.performRequest(url);
    cache.put(url, result);

    return result;
  }

  private Document performRequest(String url) {
    try {
      return Jsoup.connect(url)
          .timeout(TIMEOUT)
          .userAgent(
              "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) " +
                  "AppleWebKit/537.36 (KHTML, like Gecko) " +
                  "Chrome/56.0.2924.87 Safari/537.36")
          .get();
    } catch (IOException e) {
      LOG.error("{}: Unable to requesting URL={} with error {}", Thread.currentThread().getName(), url, e.toString());
    }

    return null;
  }
}
