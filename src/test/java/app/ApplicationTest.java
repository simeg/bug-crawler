package app;

import app.parse.Parser;
import app.persist.Persister;
import app.queue.QueueSupervisor;
import com.typesafe.config.Config;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

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
}
