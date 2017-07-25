package app.util;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public final class Utilities {

  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

  public static Optional<Document> parse(Connection.Response response) {
    try {
      return Optional.of(response.parse());

    } catch (IOException e) {
      LOG.warn("Unable to parse response from URL: [{}]", response.url());
    }

    return Optional.empty();
  }

}
