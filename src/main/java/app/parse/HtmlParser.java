package app.parse;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlParser implements Parser {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlParser.class);

  private final static int TIMEOUT = 10000;

  public List<String> queryForAttributeValues(String url, String cssQuery, String attribute) {
    try {
      return Jsoup.connect(url)
          .timeout(TIMEOUT)
          .get()
          .select(cssQuery)
          .stream()
          .map(element -> element.attr(attribute))
          .map(String::toString)
          .collect(Collectors.toList());
    } catch (IOException e) {
      LOG.error("{}: Unable to parse the URL: {}", Thread.currentThread().getName(), e.toString());
    }

    return null;
  }

  @Override
  public int getResponseStatusCode(String url) throws IOException {
    return Jsoup.connect(url)
        .userAgent(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
        .timeout(TIMEOUT)
        .execute()
        .statusCode();
  }
}
