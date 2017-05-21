package app.request;

import app.queue.PersistentQueue;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Requester {

  private static final Logger LOG = LoggerFactory.getLogger(Requester.class);

  private final RequestImpl requestImpl;
  private final PersistentQueue queue;
  private final HashMap<String, Object> cache;

  public Requester(RequestImpl requestImpl, PersistentQueue queue, HashMap<String, Object> requestCache) {
    this.requestImpl = requestImpl;
    this.queue = queue;
    this.cache = requestCache;
  }

  public CompletableFuture get(String url) {
    final CompletableFuture future = new CompletableFuture();
    queue.add(new UrlRequest(url, future));

    return future;
  }

  public Object request(String url) throws IOException {
    if (cache.containsKey(url)) {
      return cache.get(url);
    }

    final Document result = this.requestImpl.request(url);
    cache.put(url, result);

    return result;
  }
}
