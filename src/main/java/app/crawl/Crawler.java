package app.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Crawler {
  /*
   * Finds sub-links for provided URL
   */

  private static final Logger LOG = Logger.getLogger(Crawler.class.getName());

  public Set<String> getSubLinks(String url)  {
    /*
     * TODO:
     * - Cache for not working duplicates (should not live in here though, probably in Application)
     * - No other files than HTML
     * - No anchor links
     * - Normalize
     */

    if (!isValidUrl(url)) {
      LOG.log(Level.INFO, "{0}: URL not valid, will not crawl: {1}", new Object[] {Thread.currentThread().getName(), url});
      return Collections.emptySet();
    }

    final String lowercaseUrl = url.toLowerCase();
    Document document = null;
    try {
      LOG.log(Level.INFO, "{0}: Getting sub-links for URL: {1}", new Object[] {Thread.currentThread().getName(), lowercaseUrl});

      document = Jsoup.connect(lowercaseUrl).get();
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "{0}: Unable to parse the URL: {1}", new Object[] {Thread.currentThread().getName(), e.toString()});
    }

    // Select all <a> elements with an href attribute
    final Elements linkElements = document.select("a[href]");

    try {
      final String domain = getDomain(lowercaseUrl);

      return linkElements.stream()
          .distinct()
          // Get value of href attribute
          .map(element -> element.attr("href"))
          .map(link -> normalize(domain, link))
          .collect(Collectors.toSet());

    } catch (URISyntaxException e) {
      LOG.log(Level.SEVERE, "{0}: Malformed URL: {1}", new Object[] {Thread.currentThread().getName(), e.toString()});
    }

    return Collections.emptySet();
  }

  private String normalize(String domain, String link) {
    // TODO
    return link;
  }

  private String getDomain(String url) throws URISyntaxException {
    // http://stackoverflow.com/questions/9607903/get-domain-name-from-given-url
    final URI uri = new URI(url);
    final String domain = uri.getHost();
    return domain.startsWith("www.") ? domain.substring(4) : domain;
  }

  static boolean isValidUrl(String url) {
    final List<String> validProtocols = Arrays.asList("http", "https");

    final URI uri = URI.create(url);
    final String protocol = uri.getScheme();
    final String host = uri.getHost();

    return host != null && validProtocols.contains(protocol);
  }
}
