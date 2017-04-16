package app;

import app.queue.QueueSupervisor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ApplicationTest {

  private Application application;
  private QueueSupervisor supervisor;
  private ExecutorService executor;
  private Config conf;

  @Before
  public void setUp() throws Exception {
    application = new Application();
    supervisor = Mockito.mock(QueueSupervisor.class);
    executor = Mockito.mock(ExecutorService.class);
    conf = Mockito.mock(Config.class);
    application.init(application);
  }

  @Test
  public void testIsBlacklisted() throws Exception {
    final Config conf = ConfigFactory.load();
    final List<Object> blacklist = conf.getList("crawler.testBlacklist").unwrapped();

    Assert.assertTrue(application.isBlacklisted(blacklist, "blacklisted-url1.com"));
    Assert.assertTrue(application.isBlacklisted(blacklist, "blacklisted-url2.com"));

    Assert.assertFalse(application.isBlacklisted(blacklist, "non-blacklisted-url.com"));
  }
}
