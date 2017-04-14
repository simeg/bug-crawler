package app.crawl;

import app.parse.Parser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.mockito.Mockito.when;

public class CrawlerTest {

  private Crawler crawler;
  private Parser parser;

  @Before
  public void setUp() throws Exception {
    this.parser = Mockito.mock(Parser.class);
    this.crawler = new Crawler(parser);
  }

  @Test
  public void testGetSubLinks() throws Exception {
    final List<String> mockedSubLinks =
        Arrays.asList("http://specific-sub-link-1.com", "http://specific-sub-link-2.com");

    when(this.parser.queryForAttributeValues("http://www.specific-domain.com", "a[href]", "href"))
        .thenReturn(mockedSubLinks);

    final Set<String> expectedSubLinks =
        new HashSet<>(Arrays.asList("http://specific-sub-link-2.com", "http://specific-sub-link-1.com"));
    final Set<String> actualSubLinks = this.crawler.getSubLinks("http://www.specific-domain.com");

    Assert.assertEquals(expectedSubLinks, actualSubLinks);
  }

}
