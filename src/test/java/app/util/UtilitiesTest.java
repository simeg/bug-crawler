package app.util;

import org.junit.Assert;
import org.junit.Test;

public class UtilitiesTest {

  @Test
  public void testIsValidUrlFormats() throws Exception {
    final String validHttp = "http://www.arbitrary-domain.com/arbitray/";
    final String validHttpTwoSlashes = "http://www.arbitrary-domain.com//arbitray/";
    final String validNoWww = "http://arbitrary-domain.com";
    final String validHttpWww = "http://www.arbitrary-domain.com";

    Assert.assertTrue(Utilities.isValidUrl(validHttp));
    Assert.assertTrue(Utilities.isValidUrl(validHttpTwoSlashes));
    Assert.assertTrue(Utilities.isValidUrl(validNoWww));
    Assert.assertTrue(Utilities.isValidUrl(validHttpWww));

    final String missingDomain = "http://.com";
    Assert.assertFalse(Utilities.isValidUrl(missingDomain));

    final String malformedUrl1 = "http:/www.arbitrary-domain.com";
    final String malformedUrl2 = "http:www.arbitrary-domain.com";
    final String malformedUrl3 = "httpwww.arbitrary-domain.com";
    final String malformedUrl4 = "http:///www.arbitrary-domain.com";

    Assert.assertFalse(Utilities.isValidUrl(malformedUrl1));
    Assert.assertFalse(Utilities.isValidUrl(malformedUrl2));
    Assert.assertFalse(Utilities.isValidUrl(malformedUrl3));
    Assert.assertFalse(Utilities.isValidUrl(malformedUrl4));
  }

  @Test
  public void testIsValidUrlProtocols() throws Exception {
    final String validHttpUrl = "http://www.arbitrary-domain.com";
    final String validHttpsUrl = "https://www.arbitrary-domain.com";
    Assert.assertTrue(Utilities.isValidUrl(validHttpUrl));
    Assert.assertTrue(Utilities.isValidUrl(validHttpsUrl));

    final String invalidProtocol1 = "ftp://www.arbitrary-domain.com";
    final String invalidProtocol2 = "pop://www.arbitrary-domain.com";

    Assert.assertFalse(Utilities.isValidUrl(invalidProtocol1));
    Assert.assertFalse(Utilities.isValidUrl(invalidProtocol2));
  }

  @Test
  public void testNormalizeProtocol() throws Exception {
    final String slimDomain = "arbitrary-domain.com";
    final String domain = "www.arbitrary-domain.com";
    final String domainHttp = "http://www.arbitrary-domain.com";
    final String domainHttps = "https://www.arbitrary-domain.com";

    Assert.assertEquals(Utilities.normalizeProtocol(slimDomain), "http://arbitrary-domain.com");
    Assert.assertEquals(Utilities.normalizeProtocol(domain), "http://www.arbitrary-domain.com");
    Assert.assertEquals(Utilities.normalizeProtocol(domainHttp), "http://www.arbitrary-domain.com");
    Assert.assertEquals(Utilities.normalizeProtocol(domainHttps), "https://www.arbitrary-domain.com");
  }

  @Test
  public void testGetDomain() throws Exception {
    final String domain = "http://specific-domain.com";
    final String domainWww = "http://www.specific-domain.com";
    final String domainWithTrail = "http://www.specific-domain.com/arbitrary-value/arbitrary-value/";
    final String domainWithParams =
        "http://www.specific-domain.com?arbitraryParam1=arbitraryValue1&arbitraryParam2=arbitraryValue2";

    Assert.assertEquals("specific-domain.com", Utilities.getDomain(domain));
    Assert.assertEquals("specific-domain.com", Utilities.getDomain(domainWww));
    Assert.assertEquals("specific-domain.com", Utilities.getDomain(domainWithTrail));
    Assert.assertEquals("specific-domain.com", Utilities.getDomain(domainWithParams));
  }
}
