package app;

import app.parse.Parser;
import app.persist.Persister;
import app.queue.QueueSupervisor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApplicationTest {

  private Application application;
  private QueueSupervisor supervisor;
  private ExecutorService executor;
  private Config conf;
  private Parser parser;
  private Persister persister;

  @Before
  public void setUp() throws Exception {
    supervisor = Mockito.mock(QueueSupervisor.class);
    executor = Mockito.mock(ExecutorService.class);
    conf = Mockito.mock(Config.class);
    parser = Mockito.mock(Parser.class);
    persister = Mockito.mock(Persister.class);

    application = new Application();
  }

  @Test
  public void testStart() throws Exception {
    // Just to see that the application starts without any exceptions

    // Using "real" DB until I figure out how to use mocked DB
    application.init(application);
    application.start("http://www.valid-website.com", supervisor, executor, conf, parser, persister);
  }

  @Test
  public void testIsBlacklisted() throws Exception {
    final Config conf = ConfigFactory.load();
    final List<Object> blacklist = conf.getList("crawler.testBlacklist").unwrapped();

    assertTrue(Application.isBlacklisted(blacklist, "blacklisted-url1.com"));
    assertTrue(Application.isBlacklisted(blacklist, "blacklisted-url2.com"));

    assertFalse(Application.isBlacklisted(blacklist, "non-blacklisted-url.com"));
  }
}
