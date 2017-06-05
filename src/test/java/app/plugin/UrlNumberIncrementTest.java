package app.plugin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UrlNumberIncrementTest {

  @Test
  public void testIncrement() throws Exception {
    final String incrementUrl1 = "/sub-page1.html";
    assertEquals("/sub-page2.html", UrlNumberIncrement.increment(incrementUrl1));

    final String incrementUrl2 = "/sub-page11.html";
    assertEquals("/sub-page22.html", UrlNumberIncrement.increment(incrementUrl2));

    final String doNothing = "/sub-page.html";
    assertEquals("/sub-page.html", UrlNumberIncrement.increment(doNothing));
  }

  @Test
  public void testHasSubPage() throws Exception {
    final String hasSubPage = "http://wwww.domain.com/specific-sub-page.com";
    assertTrue(UrlNumberIncrement.hasSubPage(hasSubPage));

    final String noSubPage = "http://wwww.domain.com/";
    assertFalse(UrlNumberIncrement.hasSubPage(noSubPage));

    final String noSubPage2 = "http://wwww.domain.com";
    assertFalse(UrlNumberIncrement.hasSubPage(noSubPage2));
  }
}
