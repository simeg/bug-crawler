package app.parse;

import java.io.IOException;
import org.jsoup.Connection.Response;
import org.jsoup.select.Elements;

public interface Parser {

  // QUESTION:
  // This interface is still coupled to JSoup.
  // Feels wrong. Is there a way to un-couple it?

  Elements queryElements(String url, String query);

  Response getResponse(String url) throws IOException;
}
