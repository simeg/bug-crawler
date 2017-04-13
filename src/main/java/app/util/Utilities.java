package app.util;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

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
      LOG.error("{}: Unable to parse URL: {}", Thread.currentThread().getName(), url);
    }

    return null;
  }

  public static String normalizeProtocol(String url) {
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
}
