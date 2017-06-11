package app.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilitiesTest {

  @Test
  public void testIsValidUrlFormats() throws Exception {
    final String validHttp = "http://www.arb-domain.com/arb/";
    final String validHttpTwoSlashes = "http://www.arb-domain.com//arb/";
    final String validNoWww = "http://arb-domain.com";
    final String validHttpWww = "http://www.arb-domain.com";

    assertTrue(Utilities.isValidUrl(validHttp));
    assertTrue(Utilities.isValidUrl(validHttpTwoSlashes));
    assertTrue(Utilities.isValidUrl(validNoWww));
    assertTrue(Utilities.isValidUrl(validHttpWww));

    final String missingDomain = "http://.com";
    assertFalse(Utilities.isValidUrl(missingDomain));

    final String malformedUrl1 = "http:/www.arb-domain.com";
    final String malformedUrl2 = "http:www.arb-domain.com";
    final String malformedUrl3 = "httpwww.arb-domain.com";
    final String malformedUrl4 = "http:///www.arb-domain.com";

    assertFalse(Utilities.isValidUrl(malformedUrl1));
    assertFalse(Utilities.isValidUrl(malformedUrl2));
    assertFalse(Utilities.isValidUrl(malformedUrl3));
    assertFalse(Utilities.isValidUrl(malformedUrl4));
  }

  @Test
  public void testIsValidUrlProtocols() throws Exception {
    final String validHttpUrl = "http://www.arb-domain.com";
    final String validHttpsUrl = "https://www.arb-domain.com";
    assertTrue(Utilities.isValidUrl(validHttpUrl));
    assertTrue(Utilities.isValidUrl(validHttpsUrl));

    final String invalidProtocol1 = "ftp://www.arb-domain.com";
    final String invalidProtocol2 = "pop://www.arb-domain.com";

    assertFalse(Utilities.isValidUrl(invalidProtocol1));
    assertFalse(Utilities.isValidUrl(invalidProtocol2));
  }

  @Test
  public void testNormalizeProtocol() throws Exception {
    final String slimDomain = "arb-domain.com";
    final String domain = "www.arb-domain.com";
    final String domainHttp = "http://www.arb-domain.com";
    final String domainHttps = "https://www.arb-domain.com";

    assertEquals("http://www.arb-domain.com", Utilities.normalizeProtocol(domain));
    assertEquals("http://arb-domain.com", Utilities.normalizeProtocol(slimDomain));
    assertEquals("http://www.arb-domain.com", Utilities.normalizeProtocol(domainHttp));
    assertEquals("https://www.arb-domain.com", Utilities.normalizeProtocol(domainHttps));
  }

  @Test
  public void testGetDomain() throws Exception {
    final String domain = "http://specific-domain.com";
    final String domainWww = "http://www.specific-domain.com";
    final String domainWwwSlashEnd = "http://www.specific-domain.com/";
    final String domainWithTrail = "http://www.specific-domain.com/arb-value/arb-value/";
    final String domainWithParams =
        "http://www.specific-domain.com?arbParam1=arbValue1&arbParam2=arbValue2";
    final String domainWithTrailAndParams =
        "http://www.specific-domain.com/arb-value/arb-value?arbParam1=arbValue1&arbParam2=arbValue2";
    final String domainWithNestedTrailAndParams =
        "http://www.specific-domain.com/arb-value?arbParam=arbValue/arb-value?arbParam1=arbValue1&arbParam2=arbValue2";

    assertEquals("specific-domain.com", Utilities.getDomain(domain));
    assertEquals("specific-domain.com", Utilities.getDomain(domainWww));
    assertEquals("specific-domain.com", Utilities.getDomain(domainWwwSlashEnd));
    assertEquals("specific-domain.com", Utilities.getDomain(domainWithTrail));
    assertEquals("specific-domain.com", Utilities.getDomain(domainWithParams));
    assertEquals("specific-domain.com", Utilities.getDomain(domainWithTrailAndParams));
    assertEquals("specific-domain.com", Utilities.getDomain(domainWithNestedTrailAndParams));
  }
}
