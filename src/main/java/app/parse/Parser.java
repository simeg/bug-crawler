package app.parse;

import java.io.IOException;
import java.util.List;

public interface Parser {

  List<String> queryForAttributeValues(String html, String query, String attribute);

  List<String> query(String html, String query);

  int getResponseStatusCode(String url) throws IOException;

  int getHtmlHash(String url);
}
