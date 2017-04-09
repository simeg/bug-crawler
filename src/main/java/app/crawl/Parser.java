package app.crawl;

import org.jsoup.nodes.Document;
import org.jsoup.Connection.Response;

import java.io.IOException;

public interface Parser {

  // QUESTION:
  // This interface is still coupled to JSoup.
  // Feels wrong. Is there a way to un-couple it?

  Document getDocument(String url);

  Response getResponse(String url) throws IOException;
}
