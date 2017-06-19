package app.plugin;

import app.request.Requester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class UrlNumberIncrementerTest {

  private UrlNumberIncrementer urlNumberIncrement;

  @Before
  public void setUp() throws Exception {
    final Requester requester = Mockito.mock(Requester.class);
    this.urlNumberIncrement = new UrlNumberIncrementer(requester);
  }

  @Test
  public void testIncrement() throws Exception {
    final String incrementUrl1 = "/sub-page1.html";
    final String incrementUrl2 = "/sub-page11.html";
    final String doNothing = "/sub-page.html";

    assertEquals("/sub-page2.html", UrlNumberIncrementer.incrementOne(incrementUrl1));
    assertEquals("/sub-page22.html", UrlNumberIncrementer.incrementOne(incrementUrl2));
    assertEquals("/sub-page.html", UrlNumberIncrementer.incrementOne(doNothing));
  }

  @Test
  public void testHasSubPage() throws Exception {
    final String hasSubPage = "http://wwww.domain.com/arb-sub-page";
    final String hasSubPageWithExtension = "http://wwww.domain.com/arb-sub-page.php";
    final String hasSubPageAndParam = "http://wwww.domain.com/arb-sub-page?arbParam=arb-value";

    assertTrue(UrlNumberIncrementer.hasSubPage(hasSubPage));
    assertTrue(UrlNumberIncrementer.hasSubPage(hasSubPageWithExtension));
    assertTrue(UrlNumberIncrementer.hasSubPage(hasSubPageAndParam));

    final String noSubPage = "http://wwww.domain.com/";
    final String noSubPage2 = "http://wwww.domain.com";
    final String noSubPageWithParam = "http://wwww.domain.com?arbParam=arb-value";

    assertFalse(UrlNumberIncrementer.hasSubPage(noSubPage));
    assertFalse(UrlNumberIncrementer.hasSubPage(noSubPage2));
    assertFalse(UrlNumberIncrementer.hasSubPage(noSubPageWithParam));
  }

  @Test
  public void testHasNumber() throws Exception {
    final String hasNumber1 = "/arb-page0";
    final String hasNumber2 = "/arb-page-1";
    final String hasNumber3 = "/arb-page?arbParam=arbValue2";
    final String hasNumber4 = "/arb-page&arbParam=arbValue&arbParamTwo=14";

    assertTrue(UrlNumberIncrementer.hasNumber(hasNumber1));
    assertTrue(UrlNumberIncrementer.hasNumber(hasNumber2));
    assertTrue(UrlNumberIncrementer.hasNumber(hasNumber3));
    assertTrue(UrlNumberIncrementer.hasNumber(hasNumber4));

    final String hasNoNumber1 = "/arb-page";
    final String hasNoNumber2 = "/arb-page?arbParam=arbValue";

    assertFalse(UrlNumberIncrementer.hasNumber(hasNoNumber1));
    assertFalse(UrlNumberIncrementer.hasNumber(hasNoNumber2));
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

    assertEquals("", UrlNumberIncrementer.getSubPage(domain1));
    assertEquals("?param=value", UrlNumberIncrementer.getSubPage(domain2));
    assertEquals("/", UrlNumberIncrementer.getSubPage(domain3));
    assertEquals("/look-at-me", UrlNumberIncrementer.getSubPage(domain4));
    assertEquals("/look-at-me.ext", UrlNumberIncrementer.getSubPage(domain5));
    assertEquals("/look-at-me?param=value", UrlNumberIncrementer.getSubPage(domain6));
    assertEquals("/look-at-me?param=value&param2=value2", UrlNumberIncrementer.getSubPage(domain7));
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

    assertEquals("http://www.domain.com", UrlNumberIncrementer.getIncrementedUrl(domain0));
    assertEquals("http://www.domain.com", UrlNumberIncrementer.getIncrementedUrl(domain1));
    assertEquals("http://www.domain.com/", UrlNumberIncrementer.getIncrementedUrl(domain2));
    assertEquals("http://www.domain.com/subPage3.html", UrlNumberIncrementer.getIncrementedUrl(domain3));
    assertEquals("http://www.domain.com?param=value1", UrlNumberIncrementer.getIncrementedUrl(domain4));
    assertEquals("http://www.domain.com/subPage/subPage/subPage4", UrlNumberIncrementer.getIncrementedUrl(domain5));
    assertEquals("http://www.domain.com/subPage?param=value2&param=value3", UrlNumberIncrementer.getIncrementedUrl(domain6));
  }

  @Test
  public void testSetNumbersToZero() throws Exception {
    final String url1 = "/sub-page1.html";
    final String url2 = "/sub-page11.html";
    final String doNothing = "/sub-page.html";

    assertEquals("/sub-page0.html", UrlNumberIncrementer.setNumbersToZero(url1));
    assertEquals("/sub-page00.html", UrlNumberIncrementer.setNumbersToZero(url2));
    assertEquals("/sub-page.html", UrlNumberIncrementer.setNumbersToZero(doNothing));
  }

  @Test
  public void test() throws Exception {
    this.urlNumberIncrement.inspect("http://www.domain.com/subPage1.html");
  }
}
