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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class Crawler implements Runnable {
  /*
   * Shall find sub-links for provided URL
   */

  private static final Logger LOG = Logger.getLogger(Crawler.class.getName());

  @Override
  public void run() {
    // What to do with this? Does this class have to implement Runnable?
  }

  public Set<String> getSubLinks(String url)  {
    if (!isValidUrl(url)) {
      LOG.log(Level.INFO, "URL not valid, will not crawl: {0}", url);
      return Collections.emptySet();
    }

    LOG.log(Level.INFO, "Will get sub links for URL: {0}", url);

    final String lowercaseUrl = url.toLowerCase();

    Document document = null;
    try {
      document = Jsoup.connect(lowercaseUrl).get();
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Error parsing the URL: {0}", e.toString());
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
      LOG.log(Level.SEVERE, "Malformed URL: {0}", e.toString());
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

  private boolean isValidUrl(String url) {
    //URI uri = URI.create(url);
    //uri.getScheme()
    //uri.getHost()
    // if they look valid => valid URL

    // TODO: Find better way of doing this
    String urlRegex = "(http|https)://"
        + "[-A-Za-z0-9+&@#/%?=~_|!:,.;]"
        + "*[-A-Za-z0-9+&@#/%=~_|]";
    Pattern p = Pattern.compile(urlRegex);
    Matcher m = p.matcher(url);
    return m.find();
  }
}
