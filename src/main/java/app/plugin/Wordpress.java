package app.plugin;

import app.analyze.Bug;
import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static app.util.RequestUtils.getFutureResult;
import static app.util.RequestUtils.isMatching;

public class Wordpress implements Plugin {

  private static final Logger LOG = LoggerFactory.getLogger(Wordpress.class);

  private final Requester requester;

  public Wordpress(Requester requester) {
    this.requester = requester;
  }

  @Override
  public String getDescription() {
    return
        "Handles all Wordpress related bugs";
  }

  @Override
  public Set<Bug> inspect(String url) {
    if (isWordpress(url)) {
      final Set<Bug> result = Sets.newHashSet();
      final int wpVersion = getWpVersion(url);

      // TODO: Find wordpress bugs
    } else {
      LOG.info("Website is not a Wordpress instance, will not look for Wordpress bugs");
    }

    return Collections.emptySet();
  }

  private int getWpVersion(String url) {
    // TODO: https://github.com/andresriancho/w3af/blob/master/w3af/plugins/crawl/wordpress_fingerprint.py#L84
    return -1;
  }

  boolean isWordpress(String url) {
    try {
      final String wpLoginUrl = url + "/wp-login.php";

      final CompletableFuture future =
          requester.init(wpLoginUrl, UrlRequest.RequestType.STATUS_CODE);
      final int statusCode = (int) getFutureResult(future);

      final boolean isWordpressInstance =
          (statusCode == 200 && !isMatching(requester, url, wpLoginUrl));
      return isWordpressInstance;

    } catch (BadFutureException e) {
      return false;
    }
  }

}
