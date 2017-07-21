package app.crawl;

import app.parse.Parser;
import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import io.mola.galimatias.GalimatiasParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static app.util.RequestUtils.getFutureResult;
import static app.util.UrlUtils.validateUrl;

public class Crawler {
  /*
   * Finds sub-links for consumed URL
   */

  private static final Logger LOG = LoggerFactory.getLogger(Crawler.class);

  private final Requester requester;
  private final Parser parser;

  public Crawler(Requester requester, Parser parser) {
    this.requester = requester;
    this.parser = parser;
  }

  public Set<String> getSubLinks(String unvalidatedUrl) {
    try {
      final String url = validateUrl(unvalidatedUrl);

      LOG.info("Getting sub-links for URL [{}]", url);
      final CompletableFuture future = this.requester.init(url, UrlRequest.RequestType.HTML);
      final String html = String.valueOf(getFutureResult(future));

      // Select all <a> elements with an href attribute and return their href values
      // By including `abs` in the query all relative paths gets resolved into absolute paths
      final List<String> subLinks = this.parser.queryForAttributeValues(html, "a[href]", "abs:href");

      return subLinks.stream()
          .distinct()
          .map(String::toLowerCase)
          .filter(this::isValidLink)
          .collect(Collectors.toSet());

    } catch (BadFutureException e) {
      return Collections.emptySet();
    } catch (GalimatiasParseException e) {
      LOG.error(String.format("Unable to parse url [%s]", unvalidatedUrl), e);
      return Collections.emptySet();
    }
  }

  boolean isValidLink(String link) {
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

}
