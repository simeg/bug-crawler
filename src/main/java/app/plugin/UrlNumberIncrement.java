package app.plugin;

import app.analyze.Bug;
import app.request.Requester;
import app.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
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
            "saved as a vulnerability.";
  }

  @Override
  public Set<Bug> inspect(String url) {
    if (hasSubPage(url) && hasNumber(getSubPage(url))) {
      // For a set number of times
      //    Get incremented URL
      //    Request incremented URL
      //    Inspect content
    }

    return null;
  }

  public static String getIncrementedUrl(String url) {
    final String subPage = getSubPage(url);
    final String incrementedSubPage = increment(subPage);
    final String domain = Utilities.getDomain(url);
    return String.format("http://www.%s%s", domain, incrementedSubPage);
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

  public static boolean hasSubPage(String url) {
    final String domain = Utilities.getDomain(url);

    if (domain == null) {
      LOG.warn("Parsing domain from URL=[{}] gave a null response", url);
      return false;
    }

    final String subPage = getSubPage(url);

    if (subPage.length() <= 1) {
      return false;
    } else if (subPage.indexOf("?") == 0) {
      return false;
    }

    return true;
  }

  public static String getSubPage(String url) {
    try {
      final URI uri = new URI(url);
      final String query = uri.getQuery();
      final String path = uri.getPath();

      if (path.equals("") && query != null) {
        return "?" + query;
      }

      return path + (query == null ? "" : "?" + query);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Unable to parse URL", e);
    }
  }

  public static boolean hasNumber(String url) {
    return url.matches(".*\\d+.*");
  }
}
