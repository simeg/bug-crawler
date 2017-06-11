package app.plugin;

import org.junit.Test;

import static org.junit.Assert.*;

public class UrlNumberIncrementTest {

  @Test
  public void testIncrement() throws Exception {
    final String incrementUrl1 = "/sub-page1.html";
    final String incrementUrl2 = "/sub-page11.html";
    final String doNothing = "/sub-page.html";

    assertEquals("/sub-page2.html", UrlNumberIncrement.increment(incrementUrl1));
    assertEquals("/sub-page22.html", UrlNumberIncrement.increment(incrementUrl2));
    assertEquals("/sub-page.html", UrlNumberIncrement.increment(doNothing));
  }

  @Test
  public void testHasSubPage() throws Exception {
    final String hasSubPage = "http://wwww.domain.com/arb-sub-page";
    final String hasSubPageWithExtension = "http://wwww.domain.com/arb-sub-page.php";
    final String hasSubPageAndParam = "http://wwww.domain.com/arb-sub-page?arbParam=arb-value";

    assertTrue(UrlNumberIncrement.hasSubPage(hasSubPage));
    assertTrue(UrlNumberIncrement.hasSubPage(hasSubPageWithExtension));
    assertTrue(UrlNumberIncrement.hasSubPage(hasSubPageAndParam));

    final String noSubPage = "http://wwww.domain.com/";
    final String noSubPage2 = "http://wwww.domain.com";
    final String noSubPageWithParam = "http://wwww.domain.com?arbParam=arb-value";

    assertFalse(UrlNumberIncrement.hasSubPage(noSubPage));
    assertFalse(UrlNumberIncrement.hasSubPage(noSubPage2));
    assertFalse(UrlNumberIncrement.hasSubPage(noSubPageWithParam));
  }

  @Test
  public void testHasNumber() throws Exception {
    final String hasNumber1 = "/arb-page0";
    final String hasNumber2 = "/arb-page-1";
    final String hasNumber3 = "/arb-page?arbParam=arbValue2";
    final String hasNumber4 = "/arb-page&arbParam=arbValue&arbParamTwo=14";

    assertTrue(UrlNumberIncrement.hasNumber(hasNumber1));
    assertTrue(UrlNumberIncrement.hasNumber(hasNumber2));
    assertTrue(UrlNumberIncrement.hasNumber(hasNumber3));
    assertTrue(UrlNumberIncrement.hasNumber(hasNumber4));

    final String hasNoNumber1 = "/arb-page";
    final String hasNoNumber2 = "/arb-page?arbParam=arbValue";

    assertFalse(UrlNumberIncrement.hasNumber(hasNoNumber1));
    assertFalse(UrlNumberIncrement.hasNumber(hasNoNumber2));
  }

  @Test
  public void testGetSubPage() throws Exception {
    final String domain1 = "http://www.domain.com";
    final String domain2 = "http://www.domain.com?param=value";
    final String domain3 = "http://www.domain.com/";
    final String domain4 = "http://www.domain.com/look-at-me";
    final String domain5 = "http://www.domain.com/look-at-me.ext";
    final String domain6 = "http://www.domain.com/look-at-me?param=value";
    final String domain7 = "http://www.domain.com/look-at-me?param=value&param2=value2";

    assertEquals("", UrlNumberIncrement.getSubPage(domain1));
    assertEquals("?param=value", UrlNumberIncrement.getSubPage(domain2));
    assertEquals("/", UrlNumberIncrement.getSubPage(domain3));
    assertEquals("/look-at-me", UrlNumberIncrement.getSubPage(domain4));
    assertEquals("/look-at-me.ext", UrlNumberIncrement.getSubPage(domain5));
    assertEquals("/look-at-me?param=value", UrlNumberIncrement.getSubPage(domain6));
    assertEquals("/look-at-me?param=value&param2=value2", UrlNumberIncrement.getSubPage(domain7));
  }

  @Test
  public void testGetIncrementedUrl() throws Exception {
    final String domain0 = "http://domain.com";
    final String domain1 = "http://www.domain.com";
    final String domain2 = "http://www.domain.com/";
    final String domain3 = "http://www.domain.com/subPage2.html";
    final String domain4 = "http://www.domain.com?param=value0";
    final String domain5 = "http://www.domain.com/subPage/subPage/subPage3";
    final String domain6 = "http://www.domain.com/subPage?param=value1&param=value2";

    assertEquals("http://www.domain.com", UrlNumberIncrement.getIncrementedUrl(domain0));
    assertEquals("http://www.domain.com", UrlNumberIncrement.getIncrementedUrl(domain1));
    assertEquals("http://www.domain.com/", UrlNumberIncrement.getIncrementedUrl(domain2));
    assertEquals("http://www.domain.com/subPage3.html", UrlNumberIncrement.getIncrementedUrl(domain3));
    assertEquals("http://www.domain.com?param=value1", UrlNumberIncrement.getIncrementedUrl(domain4));
    assertEquals("http://www.domain.com/subPage/subPage/subPage4", UrlNumberIncrement.getIncrementedUrl(domain5));
    assertEquals("http://www.domain.com/subPage?param=value2&param=value3", UrlNumberIncrement.getIncrementedUrl(domain6));

  }
}
