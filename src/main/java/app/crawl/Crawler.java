package app.crawl;

import app.parse.Parser;
import app.request.Requester;
import app.request.UrlRequest;
import app.util.Utilities;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Crawler {
  /*
   * Finds sub-links for consumed URL
   */

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Crawler.class);

  private static final int FUTURE_TIMEOUT = 10;

  private final Requester requester;
  private final Parser parser;

  public Crawler(Requester requester, Parser parser) {
    this.requester = requester;
    this.parser = parser;
  }

  public Set<String> getSubLinks(String url) {
    final String fixedUrl = Utilities.normalizeProtocol(url.toLowerCase());

    if (!Utilities.isValidUrl(fixedUrl)) {
      LOG.info("URL not valid, will not crawl: {}", fixedUrl);
      return Collections.emptySet();
    }

    LOG.info("Getting sub-links for URL: {}", fixedUrl);
    final CompletableFuture future = this.requester.get(url, UrlRequest.RequestType.HTML);
    final String html = getHtml(future);

    // Select all <a> elements with an href attribute and return their href values
    final List<String> subLinks = this.parser.queryForAttributeValues(html, "a[href]", "href");

    final String domain = Utilities.getDomain(fixedUrl);

    if (domain != null) {
      return subLinks.stream()
          .distinct()
          .filter(this::isValidLink)
          .map(link -> normalize(domain, link))
          .collect(Collectors.toSet());
    }

    return Collections.emptySet();
  }

  private static String getHtml(CompletableFuture future) {
    while (!future.isDone()) {
      try {
        return String.valueOf(future.get(FUTURE_TIMEOUT, TimeUnit.SECONDS));

      } catch (InterruptedException e) {
        LOG.error("Error when handling future. Thread was interrupted {}", e.toString());
      } catch (ExecutionException e) {
        LOG.error("Error when handling future. Future was completed exceptionally {}", e.toString());
      } catch (TimeoutException e) {
        LOG.error("Error when handling future. Future took too long time to finish {}", e.toString());
      }
    }

    return null;
  }

  boolean isValidLink(String receivedLink) {
    final String link = receivedLink.toLowerCase().trim();
    return !(link.equals("") ||
        link.equals("/") ||
        link.contains("mailto:") ||
        link.endsWith(".exe") ||
        link.endsWith(".txt") ||
        link.endsWith(".xml") ||
        link.endsWith(".zip") ||
        link.endsWith(".rar") ||
        link.endsWith(".tar") ||
        link.endsWith(".pdf") ||
        link.endsWith(".jpg") ||
        link.endsWith(".jpeg") ||
        link.endsWith(".png") ||
        link.endsWith(".tiff") ||
        link.endsWith(".gif") ||
        link.endsWith(".bmp") ||
        link.endsWith(".exif") ||
        link.endsWith(".svg"));
  }

  private String normalize(String domain, String link) {
    // TODO
    return link;
  }
}
