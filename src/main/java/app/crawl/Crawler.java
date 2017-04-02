package app.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Crawler implements Runnable {

  private static final Logger LOG = Logger.getLogger(Crawler.class.getName());

  @Override
  public void run() {
    // What to do with this? Does this class have to implement Runnable?
  }

  public Set<String> getSubLinks(String url)  {
    String lowercaseUrl = url.toLowerCase();

    Document document = null;
    try {
      document = Jsoup.connect(lowercaseUrl).get();
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Error parsing the URL: {0}", e);
    }

    // Select all <a> elements with an href attribute
    Elements linkElements = document.select("a[href]");

    try {
      String domain = getDomain(lowercaseUrl);

      return linkElements.stream()
          .distinct()
          // Get value of href attribute
          .map(element -> element.attr("href"))
          .map(link -> normalize(domain, link))
          .collect(Collectors.toSet());

    } catch (URISyntaxException e) {
      LOG.log(Level.SEVERE, "Malformed URL: {0}", e);
    }

    return Collections.emptySet();
  }

  private String normalize(String domain, String link) {
    // TODO
    return link;
  }

  private String getDomain(String url) throws URISyntaxException {
    // http://stackoverflow.com/questions/9607903/get-domain-name-from-given-url
    URI uri = new URI(url);
    String domain = uri.getHost();
    return domain.startsWith("www.") ? domain.substring(4) : domain;
  }
}
