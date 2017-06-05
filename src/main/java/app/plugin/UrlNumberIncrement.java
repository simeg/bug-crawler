package app.plugin;

import app.analyze.Bug;
import app.request.Requester;
import app.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlNumberIncrement implements Plugin {

  private static final Logger LOG = LoggerFactory.getLogger(UrlNumberIncrement.class);

  private final Requester requester;

  public UrlNumberIncrement(Requester requester) {
    this.requester = requester;
  }

  @Override
  public String getDescription() {
    return
        "If url has sub-page that contains a " +
            "number, this plugin will increment " +
            "that number and request that page. " +
            "If the requested page is not a 404 it's " +
            "saved as a vulnerability";
  }

  @Override
  public Set<Bug> inspect(String url) {
    if (hasSubPage(url) && subPageHasNumber(url)) {
      final String subPage = getSubPage(url);
      final String touchedSubPage = increment(subPage);
    }


    return null;
  }

  public static String increment(String subPage) {
    final Pattern digitPattern = Pattern.compile("(\\d)");

    final Matcher matcher = digitPattern.matcher(subPage);
    final StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(result, String.valueOf(Integer.parseInt(matcher.group(1)) + 1));
    }
    matcher.appendTail(result);

    return result.toString();
  }

  public static String getSubPage(String url) {
    return null;
  }

  public static boolean subPageHasNumber(String url) {
    return true;
  }

  public static boolean hasSubPage(String url) {
    final String domain = Utilities.getDomain(url);

    if (domain == null) {
      LOG.warn("Parsing domain from URL={} gave a null response", url);
      return false;
    }

    return url.length() > domain.length() && !url.contains("&");

  }

}
