package app.request;

import java.util.concurrent.CompletableFuture;

public class UrlRequest {

  public enum RequestType {
    /*
     * Will return entire response object where you
     * can extract the status code, HTML, etc.
     */
    RAW,
    /*
     * Will return the body of the HTML as a string
     */
    HTML,
    /*
     * Will return the hash code of body of the HTML
     */
    HTML_HASH,
    /*
     * Will return the response status code
     */
    STATUS_CODE,
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
