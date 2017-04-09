package app.util;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public final class Utilities {

  public static boolean isValidUrl(String url) {
    final List<String> validProtocols = Arrays.asList("http", "https");

    final URI uri = URI.create(url);
    final String protocol = uri.getScheme();
    final String host = uri.getHost();

    return host != null && validProtocols.contains(protocol);
  }
}
