package app.plugin;

import app.analyze.Bug;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static app.plugin.PluginUtilities.getFutureResult;
import static app.plugin.PluginUtilities.isMatching;

public class PageFinder implements Plugin {

  private static final Logger LOG = LoggerFactory.getLogger(PageFinder.class);

  private static final List<String> pagePaths =
      Arrays.asList(
          "phpinfo.php",
          "phpmyadmin",
          "test.php?mode=phpinfo",
          ".htaccess",
          ".htaccess.bak",
          ".htpasswd",
          ".htpasswd.bak"
      );

  private final Requester requester;

  public PageFinder(Requester requester) {
    this.requester = requester;
  }

  @Override
  public String getDescription() {
    return
        "Looks for pages known for being used " +
            "for sensitive access, configuration etc.\n\n" +
            "If a page is found and it differs from the " +
            "root domain it's marked as a bug.";
  }

  @Override
  public Set<Bug> inspect(String url) {
    final Set<Bug> result = Sets.newHashSet();

    pagePaths.forEach(path -> {
      final String fullUrlPath = url + "/" + path;

      final CompletableFuture future = requester.get(url, UrlRequest.RequestType.STATUS_CODE);
      final int statusCode = (int) getFutureResult(future);

      // If the url + path has exactly the same HTML content as the url,
      // it's a false-positive and should not be reported as a potential bug.
      // Therefore we're checking here to see if they are matching.
      final boolean isMatching = isMatching(requester, url, fullUrlPath);

      if (statusCode == 200 && !isMatching) {
        LOG.info("Found file {} on URL: {}", path, url);
        result.add(
            new Bug(
                Bug.BugType.FILE_ACCESS,
                url,
                "Access to " + path,
                Optional.of(fullUrlPath)
            )
        );
      }
    });

    return result;
  }

}
