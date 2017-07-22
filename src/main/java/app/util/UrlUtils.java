package app.util;

import app.crawl.InvalidExtensionException;
import com.google.common.collect.ImmutableSet;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class UrlUtils {

  private static final Logger LOG = LoggerFactory.getLogger(UrlUtils.class);

  public static String getHost(String url) throws GalimatiasParseException {
    final String parsedUrl = URL.parse(url).host().toString();
    return parsedUrl.startsWith("www.") ? parsedUrl.substring(4) : parsedUrl;
  }

  public static String validateUrl(String unvalidatedUrl)
      throws GalimatiasParseException, InvalidExtensionException {
    // Make sure it's http or https
    String url = normalizeProtocol(unvalidatedUrl.toLowerCase());

    if (hasInvalidExtension(url)) {
      throw new InvalidExtensionException(String.format("URL has invalid extension=[%s]", url));
    }

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

  static boolean hasInvalidExtension(String link) {
    Set<String> result = ImmutableSet.of(
        "exe", "txt", "xml", "zip", "rar",
        "tar", "pdf", "jpg", "jpeg", "png",
        "tiff", "gif", "bmp", "exif", "svg")
        .stream()
        .map(ext -> "." + ext)
        .filter(link::endsWith)
        .collect(Collectors.toSet());

    return result.size() > 0;
  }

}
