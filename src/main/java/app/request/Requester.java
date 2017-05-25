package app.request;

import java.util.concurrent.CompletableFuture;

public interface Requester {

  CompletableFuture get(String url, UrlRequest.RequestType type);

  Object requestHtml(String url);

  Object requestHtmlHash(String url);

  int requestStatusCode(String url);
}
