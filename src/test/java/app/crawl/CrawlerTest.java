package app.crawl;

import app.parse.Parser;
import app.request.Requester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CrawlerTest {

  private Crawler crawler;
  private Parser parser;
  private Requester requester;

  @Before
  public void setUp() throws Exception {
    this.parser = Mockito.mock(Parser.class);
    this.requester = Mockito.mock(Requester.class);
    this.crawler = new Crawler(requester, parser);
  }

  @Test
  public void testGetSubLinks() throws Exception {
    // TODO: Figure out how to test Futures

    /*final List<String> mockedSubLinks =
        Arrays.asList("http://specific-sub-link-1.com", "http://specific-sub-link-2.com");

    when(this.parser.queryForAttributeValues("http://www.specific-domain.com", "a[href]", "href"))
        .thenReturn(mockedSubLinks);

    final Set<String> expectedSubLinks =
        new HashSet<>(Arrays.asList("http://specific-sub-link-2.com", "http://specific-sub-link-1.com"));
    final Set<String> actualSubLinks = this.crawler.getSubLinks("http://www.specific-domain.com");

    Assert.assertEquals(expectedSubLinks, actualSubLinks);*/
  }

  @Test
  public void testIsValidLink() throws Exception {
    Assert.assertFalse(this.crawler.isValidLink("document.pdf"));
    Assert.assertFalse(this.crawler.isValidLink("mailto:address"));
    Assert.assertFalse(this.crawler.isValidLink("image.png"));
    Assert.assertFalse(this.crawler.isValidLink(""));
    Assert.assertFalse(this.crawler.isValidLink("/"));

    Assert.assertTrue(this.crawler.isValidLink("http://irrelevant-website.com"));
    Assert.assertTrue(this.crawler.isValidLink("http://www.irrelevant-website.com"));
    Assert.assertTrue(this.crawler.isValidLink("http://irrelevant-website.com/link/link"));
    Assert.assertTrue(this.crawler.isValidLink("http://irrelevant-website.com?param1=value1&param2=value2"));
  }
}
