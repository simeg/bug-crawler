package app;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ApplicationTest {

  private Application application;

  @Before
  public void setUp() throws Exception {
    this.application = new Application();
    this.application.init();
  }

  @Test
  public void testAppStartsWithoutExceptions() throws Exception {
    // Just to see that application can be started without any exceptions
    final String workingWebsite = "http://www.vecka.nu";
    this.application.start(workingWebsite);
  }

  @Test
  public void testIsBlacklisted() throws Exception {
    final Config conf = ConfigFactory.load();
    final List<Object> blacklist = conf.getList("crawler.testBlacklist").unwrapped();

    Assert.assertTrue(this.application.isBlacklisted(blacklist, "blacklisted-url1.com"));
    Assert.assertTrue(this.application.isBlacklisted(blacklist, "blacklisted-url2.com"));

    Assert.assertFalse(this.application.isBlacklisted(blacklist, "non-blacklisted-url.com"));
  }
}
