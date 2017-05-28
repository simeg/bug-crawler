package app.util;

import com.google.common.collect.Sets;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public final class Utilities {

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Utilities.class);

  public static boolean isValidUrl(String url) {
    final String[] schemes = {"http", "https"};
    final UrlValidator urlValidator = new UrlValidator(schemes, 2L);
    return urlValidator.isValid(url);
  }

  public static String getDomain(String url) {
    // http://stackoverflow.com/questions/9607903/get-domain-name-from-given-url
    try {
      final URI uri = new URI(url);
      final String domain = uri.getHost();
      return domain.startsWith("www.") ? domain.substring(4) : domain;
    } catch (URISyntaxException e) {
      LOG.warn("Unable to parse URL: {}", url);
    }

    return null;
  }

  public static String normalizeProtocol(String url) {
    // https://jsoup.org/cookbook/extracting-data/working-with-urls

    /*
     * TODO:
     * - Fix anchor links
    */

    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }

    // TODO: Consider using regexp for this
    if (url.startsWith("/")) {
      return "http:/" + url;
    }

    return "http://" + url;
  }

  public static boolean isBlacklisted(String domain) {
    // TODO: Add github to list when vecka.nu is not used as
    // test website anymore
    final Set<String> blacklist = Sets.newHashSet(
        "google.com",
        "youtube.com",
        "facebook.com",
        "baidu.com",
        "wikipedia.org",
        "yahoo.com",
        "reddit.com",
        "amazon.com",
        "twitter.com",
        "instagram.com",
        "linkedin.com"
    );
    return blacklist.contains(domain);
  }
}
