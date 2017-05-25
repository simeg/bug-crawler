package app.plugin;

import app.analyze.Bug;
import app.parse.Parser;
import app.request.Requester;
import app.request.UrlRequest;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class HtmlComments implements Plugin {
  /*
   * Looks for comments in the HTML containing the
   * words 'password' and 'user'.
   */

  private static final Logger LOG = LoggerFactory.getLogger(HtmlComments.class);

  private static final int FUTURE_TIMEOUT = 10;

  private final Requester requester;
  private final Parser parser;

  public HtmlComments(Requester requester, Parser parser) {
    this.requester = requester;
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
//    result.addAll(queryPasswordForms(url));

    return result;
  }

  private Set<Bug> queryForString(String url, String query) {
    final CompletableFuture future = requester.get(url, UrlRequest.RequestType.HTML);
    final String html = getHtml(future);

    return parser.query(html, query)
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

  private static String getHtml(CompletableFuture future) {
    while (!future.isDone()) {
      try {
        return String.valueOf(future.get(FUTURE_TIMEOUT, TimeUnit.SECONDS));

      } catch (InterruptedException e) {
        LOG.error("{}: Error when handling future. Thread was interrupted {}",
            Thread.currentThread().getName(), e.toString());
      } catch (ExecutionException e) {
        LOG.error("{}: Error when handling future. Future was completed exceptionally {}",
            Thread.currentThread().getName(), e.toString());
      } catch (TimeoutException e) {
        LOG.error("{}: Error when handling future. Future took too long time to finish {}",
            Thread.currentThread().getName(), e.toString());
      }
    }

    return null;
  }
}
