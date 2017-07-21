package app.util;

import org.junit.Test;

import static app.util.UrlUtils.getHost;
import static org.junit.Assert.assertEquals;

public class UrlUtilsTest {
 /* @Test
  public void testIsValidUrlFormats() throws Exception {
    final String validHttp = "http://www.arb-domain.com/arb/";
    final String validHttpTwoSlashes = "http://www.arb-domain.com//arb/";
    final String validNoWww = "http://arb-domain.com";
    final String validHttpWww = "http://www.arb-domain.com";

    assertTrue(isValidUrl(validHttp));
    assertTrue(isValidUrl(validHttpTwoSlashes));
    assertTrue(isValidUrl(validNoWww));
    assertTrue(isValidUrl(validHttpWww));

    final String missingDomain = "http://.com";
    assertFalse(isValidUrl(missingDomain));

    final String malformedUrl1 = "http:/www.arb-domain.com";
    final String malformedUrl2 = "http:www.arb-domain.com";
    final String malformedUrl3 = "httpwww.arb-domain.com";
    final String malformedUrl4 = "http:///www.arb-domain.com";

    assertFalse(isValidUrl(malformedUrl1));
    assertFalse(isValidUrl(malformedUrl2));
    assertFalse(isValidUrl(malformedUrl3));
    assertFalse(isValidUrl(malformedUrl4));
  }

  @Test
  public void testIsValidUrlProtocols() throws Exception {
    final String validHttpUrl = "http://www.arb-domain.com";
    final String validHttpsUrl = "https://www.arb-domain.com";
    assertTrue(isValidUrl(validHttpUrl));
    assertTrue(isValidUrl(validHttpsUrl));

    final String invalidProtocol1 = "ftp://www.arb-domain.com";
    final String invalidProtocol2 = "pop://www.arb-domain.com";

    assertFalse(isValidUrl(invalidProtocol1));
    assertFalse(isValidUrl(invalidProtocol2));
  }

  @Test
  public void testNormalizeProtocol() throws Exception {
    final String slimDomain = "arb-domain.com";
    final String domain = "www.arb-domain.com";
    final String domainHttp = "http://www.arb-domain.com";
    final String domainHttps = "https://www.arb-domain.com";

    assertEquals("http://www.arb-domain.com", normalizeProtocol(domain));
    assertEquals("http://arb-domain.com", normalizeProtocol(slimDomain));
    assertEquals("http://www.arb-domain.com", normalizeProtocol(domainHttp));
    assertEquals("https://www.arb-domain.com", normalizeProtocol(domainHttps));
  }*/

  @Test
  public void testGetHost() throws Exception {
    final String host = "http://specific-host.com";
    final String hostWww = "http://www.specific-host.com";
    final String hostWwwSlashEnd = "http://www.specific-host.com/";
    final String hostWithTrail = "http://www.specific-host.com/arb-value/arb-value/";
    final String hostWithParams =
        "http://www.specific-host.com?arbParam1=arbValue1&arbParam2=arbValue2";
    final String hostWithTrailAndParams =
        "http://www.specific-host.com/arb-value/arb-value?arbParam1=arbValue1&arbParam2=arbValue2";
    final String hostWithNestedTrailAndParams =
        "http://www.specific-host.com/arb-value?arbParam=arbValue/arb-value?arbParam1=arbValue1&arbParam2=arbValue2";

    assertEquals("specific-host.com", getHost(host));
    assertEquals("specific-host.com", getHost(hostWww));
    assertEquals("specific-host.com", getHost(hostWwwSlashEnd));
    assertEquals("specific-host.com", getHost(hostWithTrail));
    assertEquals("specific-host.com", getHost(hostWithParams));
    assertEquals("specific-host.com", getHost(hostWithTrailAndParams));
    assertEquals("specific-host.com", getHost(hostWithNestedTrailAndParams));
  }
}
