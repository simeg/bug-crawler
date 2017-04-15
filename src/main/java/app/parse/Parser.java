package app.parse;

import java.io.IOException;
import java.util.List;

public interface Parser {

  List<String> queryForAttributeValues(String url, String query, String attribute);

  int getResponseStatusCode(String url) throws IOException;
}
