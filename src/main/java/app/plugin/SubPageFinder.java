package app.plugin;

import app.analyze.Bug;
import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static app.request.RequestUtil.getFutureResult;
import static app.request.RequestUtil.isMatching;

public class SubPageFinder implements Plugin {

  private static final Logger LOG = LoggerFactory.getLogger(SubPageFinder.class);
  private static final ImmutableSet<String> pagePaths =
      ImmutableSet.of(
          "phpinfo.php",
          "phpmyadmin",
          "test.php?mode=phpinfo",
          ".htaccess",
          ".htaccess.bak",
          ".htpasswd",
          ".htpasswd.bak"
      );

  private final Requester requester;

  public SubPageFinder(Requester requester) {
    this.requester = requester;
  }

  @Override
  public String getDescription() {
    return
        "Looks for pages known for being used "
            + "for sensitive access, configuration etc.\n\n"
            + "If a page is found and it differs from the "
            + "root domain it's marked as a bug.";
  }

  @Override
  public ImmutableSet<Bug> inspect(String url) {
    final Set<Bug> result = Sets.newHashSet();

    pagePaths.forEach(path -> {
      try {
        final String fullUrlPath = formatFullPath(url, path);

        final CompletableFuture future = requester.init(url, UrlRequest.RequestType.STATUS_CODE);
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
      } catch (BadFutureException ignored) {
        // Continue
      }
    });

    return ImmutableSet.copyOf(result);
  }

  private static String formatFullPath(String baseUrl, String path) {
    if (baseUrl.endsWith("/")) {
      return baseUrl + path;
    } else {
      return baseUrl + "/" + path;
    }
  }

}
