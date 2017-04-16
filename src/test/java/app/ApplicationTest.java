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
    supervisor = Mockito.mock(QueueSupervisor.class);
    executor = Mockito.mock(ExecutorService.class);
    conf = Mockito.mock(Config.class);

    application = new Application();
  }

  @Test
  public void testStart() throws Exception {
    // Just to see that the application starts without any exceptions

    // Using "real" DB until I figure out how to use mocked DB
    application.init(application);
    application.start("http://www.valid-website.com", supervisor, executor, conf);
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
