package app.parse;

import com.google.common.collect.Maps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlParser implements Parser {

  private static final Logger LOG = LoggerFactory.getLogger(HtmlParser.class);

  private final static int TIMEOUT = 10000;

  // TODO: Use cache, maybe pass as argument?
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

  private static Document getParsedHtml(String html) {
    return Jsoup.parse(html);
  }
}
