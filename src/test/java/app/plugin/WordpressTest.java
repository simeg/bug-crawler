package app.plugin;

import app.request.Requester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class WordpressTest {

  private Wordpress plugin;
  private Requester requester;

  @Before
  public void setUp() throws Exception {
    requester = Mockito.mock(Requester.class);
    plugin = new Wordpress(requester);
  }

  @Test
  public void testIsWordpress() throws Exception {
    /*// Happy
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

    assertFalse(plugin.isWordpress(url2));*/
  }
}
