package app.parse;

import java.io.IOException;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlParser implements Parser {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlParser.class);

  private final static int TIMEOUT = 10000;

  public Elements queryElements(String url, String cssQuery) {
    try {
      return Jsoup.connect(url)
          .timeout(TIMEOUT)
          .get()
          .select(cssQuery);
    } catch (IOException e) {
      LOG.error("{}: Unable to parse the URL: {}", Thread.currentThread().getName(), e.toString());
    }

    return null;
  }

  @Override
  public Response getResponse(String url) throws IOException {
    return Jsoup.connect(url)
        .userAgent(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
        .timeout(TIMEOUT)
        .execute();
  }
}
