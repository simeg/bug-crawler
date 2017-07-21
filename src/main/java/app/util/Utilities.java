package app.util;

import com.google.common.collect.Sets;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static app.util.UrlUtils.getDomain;

public final class Utilities {

  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

  public static boolean isBlacklisted(String domain) throws URISyntaxException {
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
    ).contains(getDomain(domain));
  }

  public static Optional<Document> parse(Connection.Response response) {
    try {
      return Optional.of(response.parse());

    } catch (IOException e) {
      LOG.warn("Unable to parse response from URL: [{}]", response.url());
    }

    return Optional.empty();
  }

}
