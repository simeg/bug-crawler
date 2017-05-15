package app.plugin;

import app.analyze.Bug;
import app.parse.Parser;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HtmlComments implements Plugin {
  /*
   * Looks for comments in the HTML containing the
   * words 'password' and 'user'
   */

  private static final Logger LOG = LoggerFactory.getLogger(HtmlComments.class);

  private final Parser parser;

  public HtmlComments(Parser parser) {
    this.parser = parser;
  }

  @Override
  public Set<Bug> inspect(String url) {
    final Set<Bug> result = Sets.newHashSet();

    result.addAll(getInterestingHtml(url));

    return result;
  }

  private Set<Bug> getInterestingHtml(String url) {
    final Set<Bug> result = Sets.newHashSet();

    result.addAll(queryForString(url, "admin"));
    result.addAll(queryForString(url, "password"));
    result.addAll(queryPasswordForms(url));

    return result;
  }

  private Set<Bug> queryForString(String url, String query) {
    return parser.query(url, query)
        .stream()
        .map((element) ->
            Bug.create(
                Bug.BugType.HTML,
                url,
                "String \"" + query + "\" found in HTML",
                Optional.of(url)))
        .collect(Collectors.toSet());
  }

  private Set<Bug> queryPasswordForms(String url) {
    // TODO: Find a way to look for a form containing password,
    // because this current solution will generate false-positives
    return parser.query(url, "[password]")
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
                "String \"username\" found in HTML", // Not correct
                Optional.of(url)))
        .collect(Collectors.toSet());
  }

  private void queryElements(Set<Bug> result, Set<Bug> collect) {
    result.addAll(
        collect
    );
  }
}
