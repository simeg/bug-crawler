package app.plugin;

import app.Url;
import app.analyze.Bug;
import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static app.util.RequestUtils.getFutureResult;
import static app.util.UrlUtils.hasExtension;
import static app.util.Utilities.parse;

public class PhpInfo implements Plugin {

  private static final Logger LOG = LoggerFactory.getLogger(PhpInfo.class);
  private static final ImmutableSet<String> phpInfoPaths =
      ImmutableSet.of(
          "phpinfo.php", "info.php", "test.php?mode=phpinfo",
          "index.php?view=phpinfo", "index.php?mode=phpinfo",
          "test.php?mode=phpinfo", "?mode=phpinfo", "?view=phpinfo",
          "install.php?mode=phpinfo", "admin.php?mode=phpinfo",
          "phpversion.php", "test1.php", "phpinfo1.php", "info1.php",
          "PHPversion.php", "x.php", "xx.php", "xxx.php"
      );

  private final Requester requester;

  public PhpInfo(Requester requester) {
    this.requester = requester;
  }

  @Override
  public String getDescription() {
    return
        "Checks all directories in the "
            + "provided URL for a PHP Info file";
  }

  @Override
  public Set<Bug> inspect(String urlInput) {
    Url url = new Url(urlInput);
    Set<Bug> result = Sets.newHashSet();

    StringBuilder pathToQuery = new StringBuilder(url.getFullHost());

    // Run it once on the root domain as this case will not be run by the forEach below
    final Optional<Bug> rootDomainBug = analyzeForBug(url.url, pathToQuery);
    rootDomainBug.ifPresent(result::add);

    // Filter to remove empty strings
    ImmutableList<String> pathSegments = ImmutableList.copyOf(
        url.getPathSegments()
            .stream()
            .filter(path -> !path.trim().isEmpty())
            .collect(Collectors.toList()));

    // Iterate over all segments and query each segment for all phpInfo possibilities
    for (String pathSegment : pathSegments) {
      if (isFile(pathSegment)) {
        continue;
      }

      pathToQuery.append("/").append(pathSegment);

      final Optional<Bug> bug = analyzeForBug(url.url, pathToQuery);
      bug.ifPresent(result::add);
    }

    return result;
  }

  private Optional<Bug> analyzeForBug(String rootDomain, StringBuilder pathToQuery) {
    for (String phpInfoFile : phpInfoPaths) {
      try {
        StringBuilder phpInfoPathToQuery = new StringBuilder(pathToQuery);
        phpInfoPathToQuery.append("/").append(phpInfoFile);

        CompletableFuture future =
            requester.init(phpInfoPathToQuery.toString(), UrlRequest.RequestType.RAW);
        Connection.Response response = (Connection.Response) getFutureResult(future);
        Optional<Document> document = parse(response);

        if (response.statusCode() != 404 && isPhpInfoFile(document)) {
          String phpVersion = getPhpVersion(document);
          return Optional.of(
              new Bug(
                  Bug.BugType.PHP_INFO_FILE_ACCESS,
                  rootDomain,
                  String.format("Access to phpinfo file with PHP version: [%s]", phpVersion),
                  Optional.of(phpInfoPathToQuery.toString())
              ));
        }

      } catch (BadFutureException ignored) {
      }
    }

    return Optional.empty();
  }

  private static boolean isFile(String path) {
    return hasExtension(ImmutableSet.of("html", "php", "asp", "js"), path);
  }

  private static Boolean isPhpInfoFile(Optional<Document> document) {
    // Select the header specifying the version like "PHP Version 5.6.30",
    // if it exists we know we've come to the correct website
    return document.filter(document1 -> document1.select("h1.p").size() >= 1).isPresent();
  }

  private static String getPhpVersion(Optional<Document> document) {
    if (document.isPresent()) {
      String text = document.get().select("h1.p").text();

      if (text.length() > 0) {
        /*
         * Expected value of variable `text`
         *   PHP Version 5.6.30
         * So the regular expression is extracting
         * the digits and dots in the version.
         */
        Matcher matcher = Pattern
            .compile("\\d+.\\d+.\\d+")
            .matcher(text);
        if (matcher.find()) {
          return matcher.group(0);
        }
      }
    }

    return "NOT_FOUND";
  }

}
