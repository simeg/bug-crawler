package app.util;

import org.junit.Test;

import static app.util.UrlUtils.*;
import static org.junit.Assert.assertEquals;

public class UrlUtilsTest {

  @Test
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
    final String slimUrl = "arb-url.com";
    final String url = "www.arb-url.com";
    final String urlHttp = "http://www.arb-url.com";
    final String urlHttps = "https://www.arb-url.com";

    assertEquals("http://www.arb-url.com", normalizeProtocol(url));
    assertEquals("http://arb-url.com", normalizeProtocol(slimUrl));
    assertEquals("http://www.arb-url.com", normalizeProtocol(urlHttp));
    assertEquals("https://www.arb-url.com", normalizeProtocol(urlHttps));
  }

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
