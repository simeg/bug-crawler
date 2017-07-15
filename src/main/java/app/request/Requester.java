package app.request;

import org.jsoup.nodes.Document;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Requester {

  CompletableFuture init(String url, UrlRequest.RequestType type);

  Optional<Document> requestHtml(String url);

  Optional<Integer> requestHtmlHashCode(String url);

  Optional<Integer> requestStatusCode(String url);
}
