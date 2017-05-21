package app.request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// TODO: Better name
public class RequestImpl {

  private static final Logger LOG = LoggerFactory.getLogger(RequestImpl.class);

  private static final int TIMEOUT = 10000;

  public Document request(String url) throws IOException {
    return Jsoup.connect(url)
        .timeout(TIMEOUT)
        .userAgent(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/56.0.2924.87 Safari/537.36")
        .get();
  }
}
