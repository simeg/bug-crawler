package app.crawl;

import org.jsoup.Connection;

public interface Parser {

  // QUESTION:
  // This interface becomes useless because it's
  // still coupled to JSoup. How to make it useful?
  Connection connect(String url);
}
