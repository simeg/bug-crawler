package app.crawl;

import app.parse.Parser;
import app.request.BadFutureException;
import app.request.Requester;
import app.request.UrlRequest;
import app.url.Url;
import app.url.UrlParseException;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static app.request.RequestUtil.getFutureResult;

public class Crawler {

  private static final Logger LOG = LoggerFactory.getLogger(Crawler.class);

  private final Requester requester;
  private final Parser parser;

  public Crawler(Requester requester, Parser parser) {
    this.requester = requester;
    this.parser = parser;
  }

  public ImmutableSet<Url> getSubLinks(Url url) {
    try {
      LOG.info("Getting sub-links for URL [{}]", url.rawUrl);
      final CompletableFuture future = this.requester.init(url.rawUrl, UrlRequest.RequestType.HTML);
      final String html = String.valueOf(getFutureResult(future));

      // Select all <a> elements with an href attribute and return their href values
      // By including `abs` in the query all relative paths gets resolved into absolute paths
      final List<String> subLinks =
          this.parser.queryForAttributeValues(html, "a[href]", "abs:href");

      return ImmutableSet.copyOf(subLinks.stream()
          .distinct()
          .map(Crawler::validateUrl)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toSet()));

    } catch (BadFutureException e) {
      return ImmutableSet.of();
    }
  }

  private static Optional<Url> validateUrl(String unvalidatedUrl) {
    try {
      return Optional.of(new Url(unvalidatedUrl));
    } catch (InvalidExtensionException | UrlParseException e) {
      return Optional.empty();
    }
  }

}
