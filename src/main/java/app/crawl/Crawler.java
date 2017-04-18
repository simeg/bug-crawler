package app.crawl;

import app.parse.Parser;
import app.util.Utilities;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Crawler {
  /*
   * Finds sub-links for consumed URL
   */

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Crawler.class);

  private final Parser parser;

  public Crawler(Parser parser) {
    this.parser = parser;
  }

  public Set<String> getSubLinks(String url) {
    /*
     * TODO:
     * - Cache for not working duplicates (should not live in here though, probably in Application) - TODO
     */

    final String fixedUrl = Utilities.normalizeProtocol(url.toLowerCase());

    if (!Utilities.isValidUrl(fixedUrl)) {
      LOG.info("{}: URL not valid, will not crawl: {}", Thread.currentThread().getName(), fixedUrl);
      return Collections.emptySet();
    }

    LOG.info("{}: Getting sub-links for URL: {}", Thread.currentThread().getName(), fixedUrl);

    // Select all <a> elements with an href attribute and return their href values
    final List<String> subLinks = this.parser.queryForAttributeValues(fixedUrl, "a[href]", "href");

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

  boolean isValidLink(String receivedLink) {
    final String link = receivedLink.toLowerCase().trim();
    return !(link.equals("") ||
        link.equals("/") ||
        link.contains("mailto:") ||
        link.endsWith(".exe") ||
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
