package app.crawl;

import app.parse.Parser;
import app.util.Utilities;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;

public class Crawler {
  /*
   * Finds sub-links for consumed URL
   */

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Crawler.class);

  private final Parser parser;

  public Crawler(Parser parser) {
    this.parser = parser;
  }

  static String getDomain(String url) {
    // http://stackoverflow.com/questions/9607903/get-domain-name-from-given-url
    try {
      final URI uri = new URI(url);
      final String domain = uri.getHost();
      return domain.startsWith("www.") ? domain.substring(4) : domain;
    } catch (URISyntaxException e) {
      LOG.error("{}: Unable to parse URL: {}", Thread.currentThread().getName(), url);
    }

    return null;
  }

  public Set<String> getSubLinks(String url) {
    /*
     * TODO:
     * - Cache for not working duplicates (should not live in here though, probably in Application)
     * - No other files than HTML
     * - No anchor links
     * - Normalize
     */

    if (!Utilities.isValidUrl(url)) {
      LOG.info("{}: URL not valid, will not crawl: {}", Thread.currentThread().getName(), url);
      return Collections.emptySet();
    }

    final String lowercaseUrl = url.toLowerCase();
    LOG.info("{}: Getting sub-links for URL: {}", Thread.currentThread().getName(), lowercaseUrl);

    // Select all <a> elements with an href attribute
    final Elements linkElements = this.parser.queryElements(lowercaseUrl, "a[href]");

    final String domain = getDomain(lowercaseUrl);

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
