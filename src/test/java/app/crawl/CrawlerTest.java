package app.crawl;

import static org.junit.Assert.assertEquals;

import app.parse.Parser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CrawlerTest {

  private Crawler crawler;

  @Before
  public void setUp() throws Exception {
    Parser mockedParser = Mockito.mock(Parser.class);
    this.crawler = new Crawler(mockedParser);
  }

  @Test
  public void testGetDomain() throws Exception {
    final String domain = "http://specific-domain.com";
    final String domainWww = "http://www.specific-domain.com";

    assertEquals("specific-domain.com", Crawler.getDomain(domain));
    assertEquals("specific-domain.com", Crawler.getDomain(domainWww));
  }

  @Test
  public void testGetSubLinks() throws Exception {
    // TODO
  }
}
