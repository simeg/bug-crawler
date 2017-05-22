package app.parse;

import com.google.common.collect.Maps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlParser implements Parser {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlParser.class);

  private final static int TIMEOUT = 10000;

  private final HashMap<String, Document> cache;

  private HtmlParser(HashMap<String, Document> cache) {
    this.cache = cache;
  }

  public static HtmlParser create() {
    return new HtmlParser(Maps.newHashMap());
  }

  @Override
  public List<String> queryForAttributeValues(String html, String cssQuery, String attribute) {
    final Document document = getParsedHtml(html);

    if (document != null) {
      return document
          .select(cssQuery)
          .stream()
          .map(element -> element.attr(attribute))
          .map(String::toString)
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  @Override
  public List<String> query(String html, String cssQuery) {
    final Document document = getParsedHtml(html);

    if (document != null) {
      return document
          .select(cssQuery)
          .stream()
          .map(Element::toString)
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
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

  @Override
  public int getHtmlHash(String url) {
    final Document doc = getDocument(url);
    if (doc != null) {
      return doc.body().html().hashCode();
    }

    return -1;
  }

  Document getDocument(String url) {
    try {
      Document document = cache.get(url);

      if (document == null) {
        document = Jsoup.connect(url)
            .timeout(TIMEOUT)
            .get();
        cache.put(url, document);
      }

      return document;
    } catch (IOException e) {
      LOG.error("{}: Unable to parse the URL: {}", Thread.currentThread().getName(), e.toString());
    }

    return null;
  }

  private static Document getParsedHtml(String html) {
    return Jsoup.parse(html);
  }
}
