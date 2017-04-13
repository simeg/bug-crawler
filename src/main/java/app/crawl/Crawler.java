package app.crawl;

import app.parse.Parser;
import app.util.Utilities;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

import java.util.Collections;
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
     * - No other files than HTML - TODO
     */

    final String fixedUrl = Utilities.normalizeProtocol(url.toLowerCase());

    if (!Utilities.isValidUrl(fixedUrl)) {
      LOG.info("{}: URL not valid, will not crawl: {}", Thread.currentThread().getName(), fixedUrl);
      return Collections.emptySet();
    }

    LOG.info("{}: Getting sub-links for URL: {}", Thread.currentThread().getName(), fixedUrl);

    // Select all <a> elements with an href attribute
    final Elements linkElements = this.parser.queryElements(fixedUrl, "a[href]");

    final String domain = Utilities.getDomain(fixedUrl);

    if (domain != null) {
      return linkElements.stream()
          .distinct()
          // Get value of href attribute
          .map(element -> element.attr("href"))
          .map(link -> normalize(domain, link))
          .collect(Collectors.toSet());
    }

    return Collections.emptySet();
  }

  private String normalize(String domain, String link) {
    // TODO
    return link;
  }
}
