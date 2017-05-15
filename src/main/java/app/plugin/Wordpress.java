package app.plugin;

import app.analyze.Bug;
import app.parse.Parser;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class Wordpress implements Plugin {
  /*
   * Handles all Wordpress related bugs. Will only
   * actually do something if the website is a
   * Wordpress instance.
   */

  private static final Logger LOG = LoggerFactory.getLogger(Wordpress.class);

  private final Parser parser;

  public Wordpress(Parser parser) {
    this.parser = parser;
  }

  @Override
  public Set<Bug> inspect(String url) {
    if (isWordpress(url)) {
      final Set<Bug> result = Sets.newHashSet();
      final int wpVersion = getWpVersion(url);

      // TODO: Find wordpress bugs
    } else {
      LOG.info(
          "{}: Website is not a Wordpress instance, will not look for Wordpress bugs",
          Thread.currentThread().getName()
      );
    }

    return Collections.emptySet();
  }

  private int getWpVersion(String url) {
    // TODO: https://github.com/andresriancho/w3af/blob/master/w3af/plugins/crawl/wordpress_fingerprint.py#L84
    return -1;
  }

  boolean isWordpress(String url) {
    final String wpLoginPage = url + "/wp-login.php";
    try {
      // TODO: Make this request go through queue, not "on the side"
      return parser.getResponseStatusCode(wpLoginPage) == 200 && !isMatching(parser, url, wpLoginPage);
    } catch (IOException e) {
      LOG.error("{}: Error parsing URL: {}", Thread.currentThread().getName(), wpLoginPage);
    }

    return false;
  }

  private boolean isMatching(Parser parser, String url, String fullUrlPath) {
    final int urlHash = parser.getHtmlHash(url);
    final int pathHash = parser.getHtmlHash(fullUrlPath);
    return urlHash == pathHash;
  }
}
