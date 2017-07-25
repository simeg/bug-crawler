package app.url;

public class UrlTest {

  // TODO: Re-enable with mocked Url object

/*  @Test
  public void testValidateUrl() throws Exception {
    String url = "http://specific-host.com";
    String urlWww = "http://www.specific-host.com";
    String urlWwwSlashEnd = "http://www.specific-host.com/";
    String urlHttps = "https://www.specific-host.com/";

    assertEquals("http://specific-host.com/", validateUrl(url));
    assertEquals("http://www.specific-host.com/", validateUrl(urlWww));
    assertEquals("http://www.specific-host.com/", validateUrl(urlWwwSlashEnd));
    assertEquals("https://www.specific-host.com/", validateUrl(urlHttps));
  }

  @Test
  public void testNormalizeProtocol() throws Exception {
    final String slimUrl = "arb-rawUrl.com";
    final String url = "www.arb-rawUrl.com";
    final String urlHttp = "http://www.arb-rawUrl.com";
    final String urlHttps = "https://www.arb-rawUrl.com";

    assertEquals("http://www.arb-rawUrl.com", normalizeProtocol(url));
    assertEquals("http://arb-rawUrl.com", normalizeProtocol(slimUrl));
    assertEquals("http://www.arb-rawUrl.com", normalizeProtocol(urlHttp));
    assertEquals("https://www.arb-rawUrl.com", normalizeProtocol(urlHttps));
  }

  @Test
  public void testIsBlacklisted() throws Exception {
    String blacklistedUrl = "https://www.google.com";
    String blacklistedUrl2 = "https://google.com";

    assertTrue(isBlacklisted(blacklistedUrl));
    assertTrue(isBlacklisted(blacklistedUrl2));

    String notBlacklistedUrl = "https://www.arbitrary-domain.com";
    assertFalse(isBlacklisted(notBlacklistedUrl));
  }

  @Test
  public void testHasInvalidExtension() throws Exception {
    String validExtension1 = "https://www.arb.com";
    String validExtension2 = "https://www.arb.org";

    assertFalse(hasInvalidExtension(validExtension1));
    assertFalse(hasInvalidExtension(validExtension2));

    String invalidExtension1 = "https://www.arb.jpeg";
    String invalidExtension2 = "https://www.arb.pdf";

    assertTrue(hasInvalidExtension(invalidExtension1));
    assertTrue(hasInvalidExtension(invalidExtension2));
  }*/
}
