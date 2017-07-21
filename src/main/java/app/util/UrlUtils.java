package app.util;

import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

public class UrlUtils {

  private static final Logger LOG = LoggerFactory.getLogger(UrlUtils.class);

  public static String getHost(String url) throws URISyntaxException, GalimatiasParseException {
    final String parsedUrl = URL.parse(url).host().toString();
    return parsedUrl.startsWith("www.") ? parsedUrl.substring(4) : parsedUrl;
  }

  public static String validateUrl(String unvalidatedUrl) throws GalimatiasParseException {
    // Make sure it's http or https
    String url = normalizeProtocol(unvalidatedUrl.toLowerCase());

    // Use external library to validate url
    return URL.parse(url).toString();
  }

  static String normalizeProtocol(String url) {
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }

    // Fallback to non-SSL
    return "http://" + url;
  }

}
