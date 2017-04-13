package app.crawl;

import app.parse.Parser;
import org.junit.Before;
import org.mockito.Mockito;

public class CrawlerTest {

  private Crawler crawler;

  @Before
  public void setUp() throws Exception {

    final Parser mockedParser = Mockito.mock(Parser.class);
    this.crawler = new Crawler(mockedParser);
  }

}
