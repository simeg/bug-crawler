package app.plugin;

import app.analyze.Bug;
import app.parse.Parser;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HtmlInspector implements Plugin<Bug> {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlInspector.class);

  private final Parser parser;

  public HtmlInspector(Parser parser) {
    this.parser = parser;
  }

  @Override
  public Set<Bug> inspect(String url) {
    final Set<Bug> result = Sets.newHashSet();

    result.addAll(findHtmlBugs(url));

    return result;
  }

  public Set<Bug> findHtmlBugs(String url) {
    final Set<Bug> result = Sets.newHashSet();

    if (isWordpress(url)) {
      final int wpVersion = getWpVersion(url);
      // TODO: Find wordpress bugs
    }

    queryElements(result, getInterestingHtml(url));

    System.out.println(result);

    return result;
  }

  private Set<Bug> getInterestingHtml(String url) {
    final Set<Bug> result = Sets.newHashSet();

    queryElements(result, parser.query(url, "admin")
        .stream()
        .map((element) ->
            Bug.create(
                Bug.BugType.HTML,
                url,
                "String \"admin\" found in HTML",
                Optional.of(url)))
        .collect(Collectors.toSet()));

    queryElements(result, parser.query(url, "[password]")
        .stream()
//            .filter((element) -> !(element.contains("form") || element.contains("input")))
        .map((e) -> {
          System.out.println(e);
          return e;
        })
        .map((element) ->
            Bug.create(
                Bug.BugType.HTML,
                url,
                "String \"username\" found in HTML",
                Optional.of(url)))
        .collect(Collectors.toSet()));

    queryElements(result, parser.query(url, "password")
        .stream()
//            .filter((element) -> !(element.contains("form") || element.contains("input")))
        .map((element) ->
            Bug.create(
                Bug.BugType.HTML,
                url,
                "String \"password\" found in HTML",
                Optional.of(url)))
        .collect(Collectors.toSet()));

    return result;
  }

  private void queryElements(Set<Bug> result, Set<Bug> collect) {
    result.addAll(
        collect
    );
  }

  private int getWpVersion(String url) {
    // TODO: https://github.com/andresriancho/w3af/blob/master/w3af/plugins/crawl/wordpress_fingerprint.py#L84
    return -1;
  }

  boolean isWordpress(String url) {
    final String wpLoginPage = url + "/wp-login.php";
    try {
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
