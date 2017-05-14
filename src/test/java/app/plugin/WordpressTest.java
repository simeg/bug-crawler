package app.plugin;

import app.parse.Parser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class WordpressTest {

  private Wordpress plugin;
  private Parser parser;

  @Before
  public void setUp() throws Exception {
    parser = Mockito.mock(Parser.class);
    plugin = new Wordpress(parser);
  }

  @Test
  public void testIsWordpress() throws Exception {
    // Happy
    final String url = "http://irrelevant-url.com";
    final String wpWebsite = url + "/wp-login.php";

    when(parser.getResponseStatusCode(wpWebsite)).thenReturn(200);
    when(parser.getHtmlHash(url)).thenReturn(100);
    when(parser.getHtmlHash(wpWebsite)).thenReturn(101);

    assertTrue(plugin.isWordpress(url));

    // Sad
    final String url2 = "http://irrelevant-url.com";
    final String wpWebsite2 = url2 + "/wp-login.php";

    when(parser.getResponseStatusCode(wpWebsite2)).thenReturn(200);
    when(parser.getHtmlHash(url2)).thenReturn(100);
    when(parser.getHtmlHash(wpWebsite2)).thenReturn(100);

    assertFalse(plugin.isWordpress(url2));
  }
}
