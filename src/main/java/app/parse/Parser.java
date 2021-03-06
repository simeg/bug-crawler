package app.parse;

import java.util.List;

public interface Parser {

  List<String> queryForAttributeValues(String html, String query, String attribute);

  List<String> query(String html, String query);

}
