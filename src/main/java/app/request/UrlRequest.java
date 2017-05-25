package app.request;

import java.util.concurrent.CompletableFuture;

public class UrlRequest {

  public enum RequestType {
    HTML, HTML_HASH, STATUS_CODE,
  }

  public final String url;
  public final CompletableFuture future;
  public final RequestType type;

  public UrlRequest(String url, CompletableFuture future, RequestType type) {
    this.url = url;
    this.future = future;
    this.type = type;
  }
}
