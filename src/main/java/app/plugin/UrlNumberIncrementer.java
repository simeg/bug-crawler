package app.plugin;

import app.analyze.Bug;
import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.mola.galimatias.GalimatiasParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static app.request.RequestUtil.getFutureResult;
import static app.request.RequestUtil.isMatching;
import static app.url.UrlUtil.getHost;

public class UrlNumberIncrementer implements Plugin {

  private static final Logger LOG = LoggerFactory.getLogger(UrlNumberIncrementer.class);

  private final Requester requester;

  public UrlNumberIncrementer(Requester requester) {
    this.requester = requester;
  }

  @Override
  public String getDescription() {
    return
        "If url has sub-page that contains a "
            + "number, this plugin will increment "
            + "that number and request that page. "
            + "If the requested page is not a 404 it's "
            + "saved as a vulnerability.";
  }

  @Override
  public ImmutableSet<Bug> inspect(String url) {
    try {
      if (hasSubPage(url) && hasNumber(getSubPage(url))) {
        final Set<Bug> result = Sets.newHashSet();

        // Go from 0 - 10
        for (int i = 0; i < 10; i++) {
          final String subPage = getSubPage(url);
          final String subPageWithZero = replaceDigitsWith(subPage, String.valueOf(i));
          final String fullUrl = getFullUrl(url, subPageWithZero);

          final CompletableFuture future =
              requester.init(fullUrl, UrlRequest.RequestType.STATUS_CODE);
          final int statusCode = (int) getFutureResult(future);

          // If the url + path has exactly the same HTML content as the url,
          // it's a false-positive and should not be reported as a potential bug.
          // Therefore we're checking here to see if they are matching.
          final boolean isMatching = isMatching(requester, url, fullUrl);

          if (statusCode == 200 && !isMatching) {
            LOG.info("Found file {} on URL: {}", fullUrl, url);
            result.add(
                new Bug(
                    Bug.BugType.FILE_ACCESS,
                    url,
                    "Access to " + fullUrl,
                    Optional.of(fullUrl)
                )
            );
          }
        }

        return ImmutableSet.copyOf(result);
      }

      // TODO: Test this method

      return ImmutableSet.of();

    } catch (BadFutureException e) {
      return ImmutableSet.of();
    } catch (URISyntaxException | GalimatiasParseException e) {
      LOG.error(String.format("Unable to parse url [%s]", url), e);
      return ImmutableSet.of();
    }
  }

  private static String getFullUrl(String url, String subPageWithZero)
      throws GalimatiasParseException {
    return String.format("http://www.%s%s", getHost(url), subPageWithZero);
  }

  public static String getIncrementedUrl(String url) throws GalimatiasParseException {
    final String subPage = getSubPage(url);
    final String incrementedSubPage = incrementOne(subPage);
    final String host = getHost(url);
    return String.format("http://www.%s%s", host, incrementedSubPage);
  }

  public static boolean hasSubPage(String url) throws URISyntaxException {
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

  public static String setNumbersToZero(String url) {
    return replaceDigitsWith(url, String.valueOf(0));
  }

  public static String incrementOne(String url) {
    final Pattern digitPattern = Pattern.compile("(\\d)");

    final Matcher matcher = digitPattern.matcher(url);
    final StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(result, String.valueOf(Integer.parseInt(matcher.group(1)) + 1));
    }
    matcher.appendTail(result);

    return result.toString();
  }

  private static String replaceDigitsWith(String url, String replacement) {
    final Pattern digitPattern = Pattern.compile("(\\d)");

    final Matcher matcher = digitPattern.matcher(url);
    final StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(result, replacement);
    }
    matcher.appendTail(result);

    return result.toString();
  }
}
