package app.parse;

import org.jsoup.Connection.Response;

import java.io.IOException;
import java.util.List;

public interface Parser {

  // QUESTION:
  // This interface is still coupled to JSoup.
  // Advantages of un-coupling it: Easy to test
  // Disadvantaged of un-coupling it: Can't use JSoup methods

  List<String> queryForAttributeValues(String url, String query, String attribute);

  Response getResponse(String url) throws IOException;
}
