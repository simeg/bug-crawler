package app.url;

import com.google.common.collect.ImmutableSet;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;

import java.util.Set;
import java.util.stream.Collectors;

public final class UrlUtil {

  // TODO: Replace with Url class method getHost
  public static String getHost(String url) throws GalimatiasParseException {
    final String parsedUrl = URL.parse(url).host().toString();
    return parsedUrl.startsWith("www.") ? parsedUrl.substring(4) : parsedUrl;
  }

  public static boolean hasExtension(ImmutableSet<?> extensions, String link) {
    Set<String> result = extensions
        .stream()
        .map(ext -> "." + ext)
        .filter(link::endsWith)
        .collect(Collectors.toSet());

    return result.size() > 0;
  }

}
