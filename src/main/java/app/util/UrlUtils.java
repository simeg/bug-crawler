package app.util;

import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlUtils {

  private static final Logger LOG = LoggerFactory.getLogger(UrlUtils.class);

  public static String getDomain(String url) throws URISyntaxException {
    // TODO: Replace with URL.parse(url).host()?
    // http://stackoverflow.com/questions/9607903/get-domain-name-from-given-url
    final String domain = new URI(url).getHost();
    return domain.startsWith("www.") ? domain.substring(4) : domain;
  }

  public static String validateUrl(String unvalidatedUrl) throws GalimatiasParseException {
    // Make sure it's http or https
    String url = normalizeProtocol(unvalidatedUrl.toLowerCase());

    // Use external library to validate url
    return URL.parse(url).toString();
  }

  private static String normalizeProtocol(String url) {
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }

    // Fallback to non-SSL
    return "http://" + url;
  }

}
