package app.plugin;

import app.analyze.Bug;
import app.parse.Parser;
import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static app.util.RequestUtils.getFutureResult;

public class HtmlComments implements Plugin {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlComments.class);

  private final Requester requester;
  private final Parser parser;

  public HtmlComments(Requester requester, Parser parser) {
    this.requester = requester;
    this.parser = parser;
  }

  @Override
  public String getDescription() {
    return
        "Looks for comments in the HTML "
            + "containing the words 'password' and 'admin'";
  }

  @Override
  public ImmutableSet<Bug> inspect(String url) {
    return ImmutableSet.copyOf(Sets.union(
        queryForString(url, "admin"),
        queryForString(url, "password")
    ));
  }

  private Set<Bug> queryForString(String url, String query) {
    try {
      final CompletableFuture future = requester.init(url, UrlRequest.RequestType.HTML);
      final String html = String.valueOf(getFutureResult(future));

      return parser.query(html, query)
          .stream()
          .map((element) ->
              new Bug(
                  Bug.BugType.HTML,
                  url,
                  "String \"" + query + "\" found in HTML",
                  Optional.of(url)))
          .collect(Collectors.toSet());

    } catch (BadFutureException e) {
      return Collections.emptySet();
    }
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
            new Bug(
                Bug.BugType.HTML,
                url,
                "String \"username\" found in HTML", // Not correct
                Optional.of(url)))
        .collect(Collectors.toSet());
  }

}
