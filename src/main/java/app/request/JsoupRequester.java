package app.request;

import app.queue.SimpleQueue;
import app.parse.ParseUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class JsoupRequester implements Requester {

  private static final Logger LOG = LoggerFactory.getLogger(JsoupRequester.class);

  private static final int TIMEOUT_MS = 3000;
  private static final String USER_AGENT =
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36"
          + " (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

  private final SimpleQueue<UrlRequest> queue;
  private final HashMap<String, Connection.Response> cache;

  public JsoupRequester(
      SimpleQueue<UrlRequest> queue,
      HashMap<String, Connection.Response> requestCache) {
    this.queue = queue;
    this.cache = requestCache;
  }

  @Override
  public CompletableFuture init(String url, UrlRequest.RequestType type) {
    final CompletableFuture future = new CompletableFuture();
    queue.add(new UrlRequest(url, future, type));

    return future;
  }

  @Override
  public Optional<Connection.Response> request(String url) {
    return makeRequest(url);
  }

  @Override
  public Optional<Document> requestHtml(String url) {
    return getParsedResponse(url);
  }

  @Override
  public Optional<Integer> requestHtmlHashCode(String url) {
    Optional<Document> response = getParsedResponse(url);

    // Only hash the content of the <body> element
    return response.map(document -> document.body().html().hashCode());
  }

  @Override
  public Optional<Integer> requestStatusCode(String url) {
    Optional<Connection.Response> response = makeRequest(url);

    // Fallback to 404 if any exception upstream was thrown,
    // it might not always be the correct status code but it works for now
    return response
        .map(response1 -> Optional.of(response1.statusCode()))
        .orElseGet(() -> Optional.of(404));
  }

  private Optional<Connection.Response> makeRequest(String url) {
    try {
      if (cache.containsKey(url)) {
        return Optional.of(cache.get(url));
      }

      return Optional.of(
          Jsoup.connect(url)
              .timeout(TIMEOUT_MS)
              .userAgent(USER_AGENT)
              .execute());

    } catch (MalformedURLException e) {
      LOG.warn("Malformed URL: {}", url);
      return Optional.empty();

    } catch (IOException e) {
      // If status != 200 then HttpStatusException will be thrown and caught here
      return Optional.empty();

    } catch (Throwable e) {
      throw new RuntimeException(String.format("Unable to get requested URL: [%s]", url), e);
    }
  }

  private Optional<Document> getParsedResponse(String url) {
    Optional<Connection.Response> response = this.makeRequest(url);
    return response.flatMap(ParseUtil::parse);
  }
}
