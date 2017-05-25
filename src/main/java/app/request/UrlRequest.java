package app.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class UrlRequest {

  public enum RequestType {
    HTML, STATUS_CODE,
  }

  private static final Logger LOG = LoggerFactory.getLogger(UrlRequest.class);

  public final String url;
  public final CompletableFuture future;
  public final RequestType type;

  public UrlRequest(String url, CompletableFuture future, RequestType type) {
    this.url = url;
    this.future = future;
    this.type = type;
  }

}
