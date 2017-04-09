package app.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilitiesTest {

  @Test
  public void testIsValidUrl() throws Exception {
    // Good
    final String validHttpUrl = "http://www.irrelevant-value.com";
    final String validHttpsUrl = "https://www.irrelevant-value.nu";

    assertTrue("HTTP protocol", Utilities.isValidUrl(validHttpUrl));
    assertTrue("HTTPs protocol", Utilities.isValidUrl(validHttpsUrl));

    // Bad
    final String missingDomain = "http://.com";
    assertFalse("Missing domain", Utilities.isValidUrl(missingDomain));

    final String invalidProtocol1 = "ftp://www.irrelevant-value.com";
    final String invalidProtocol2 = "pop://www.irrelevant-value.com";

    assertFalse("FTP protocol", Utilities.isValidUrl(invalidProtocol1));
    assertFalse("POP protocol", Utilities.isValidUrl(invalidProtocol2));

    final String malformedUrl1 = "http:/www.irrelevant-value.com";
    final String malformedUrl2 = "http:www.irrelevant-value.com";
    final String malformedUrl3 = "httpwww.irrelevant-value.com";
    final String malformedUrl4 = "http:///www.irrelevant-value.com";

    assertFalse("Malformed URL", Utilities.isValidUrl(malformedUrl1));
    assertFalse("Malformed URL", Utilities.isValidUrl(malformedUrl2));
    assertFalse("Malformed URL", Utilities.isValidUrl(malformedUrl3));
    assertFalse("Malformed URL", Utilities.isValidUrl(malformedUrl4));
  }
}
