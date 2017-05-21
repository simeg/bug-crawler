package app.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

// TODO: Come up with a better name
public class UrlRequest {

  private static final Logger LOG = LoggerFactory.getLogger(UrlRequest.class);

  public final String url;
  public final CompletableFuture future;

  public UrlRequest(String url, CompletableFuture future) {
    this.url = url;
    this.future = future;
  }
}
