package app.util;

import org.apache.commons.validator.routines.UrlValidator;

public final class Utilities {

  public static boolean isValidUrl(String url) {
    final String[] schemes = {"http", "https"};
    final UrlValidator urlValidator = new UrlValidator(schemes, 2L);
    return urlValidator.isValid(url);
  }
}
