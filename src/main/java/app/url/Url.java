package app.url;

import app.crawl.InvalidExtensionException;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.mola.galimatias.GalimatiasParseException;
import io.mola.galimatias.URL;
import okhttp3.HttpUrl;
import org.apache.el.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static app.url.UrlUtils.hasExtension;

public final class Url {

  private static final Logger LOG = LoggerFactory.getLogger(Url.class);

  public String rawUrl;
  private HttpUrl httpUrl;

  public Url(String rawUrl) throws GalimatiasParseException, InvalidExtensionException, ParseException {
    this.rawUrl = rawUrl;
    String validatedUrl = validateAndNormalize(rawUrl);
    this.httpUrl = HttpUrl.parse(validatedUrl);
    if (this.httpUrl == null) {
      // TODO: Log exception to file/Sentry
      throw new ParseException(String.format("Could not parse URL=[%s]", rawUrl));
    } else if (isBlacklisted(this.getHost())) {
      // TODO: Log exception to file/Sentry
      throw new ParseException(String.format("Blacklisted URL=[%s]", this.getHost()));
    }
  }

  public List<String> getPathSegments() {
    return httpUrl.encodedPathSegments();
  }

  public String getHost() {
    // Returns "domain.com"
    // TODO: Verify and perhaps handle that it returns www.domain.com and not domain.com
    return httpUrl.host();
  }

  public String getProtocol() {
    return httpUrl.scheme();
  }

  public String getFullHost() {
    // Returns "https://www.domain.com"
    return this.getProtocol() + "://" + this.getHost();
  }

  /*
   * Validation and normalization
   */
  private static String validateAndNormalize(String unvalidatedUrl)
      throws GalimatiasParseException, InvalidExtensionException {
    // Make sure it's http or https
    String url = normalizeProtocol(unvalidatedUrl.toLowerCase());

    if (hasInvalidExtension(url)) {
      throw new InvalidExtensionException(String.format("URL has invalid extension=[%s]", url));
    }

    return URL.parse(url).toString();
  }

  private static String normalizeProtocol(String url) {
    if (url.startsWith("http://") || url.startsWith("https://")) {
      return url;
    }

    // Fallback to non-SSL
    return "http://" + url;
  }

  private static boolean hasInvalidExtension(String link) {
    ImmutableSet<String> invalidExtensions = ImmutableSet.of(
        "exe", "txt", "xml", "zip", "rar",
        "tar", "pdf", "jpg", "jpeg", "png",
        "tiff", "gif", "bmp", "exif", "svg");
    return hasExtension(invalidExtensions, link);
  }

  private static boolean isBlacklisted(String url) {
    return Sets.newHashSet(
        "localhost",
        "127.0.0.1",
        "google.com",
        "youtube.com",
        "facebook.com",
        "baidu.com",
        "wikipedia.org",
        "yahoo.com",
        "reddit.com",
        "amazon.com",
        "twitter.com",
        "instagram.com",
        "linkedin.com",
        "github.com"
    ).contains(url);
  }
}
