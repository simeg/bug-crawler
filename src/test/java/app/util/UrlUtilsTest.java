package app.util;

import org.junit.Test;

import static app.url.UrlUtils.getHost;
import static org.junit.Assert.assertEquals;

public class UrlUtilsTest {

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
