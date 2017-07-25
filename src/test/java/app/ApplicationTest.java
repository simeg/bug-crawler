package app;

import app.parse.Parser;
import app.persist.Persister;
import app.queue.QueueId;
import app.queue.QueueSupervisor;
import app.queue.SimpleQueue;
import app.request.Requester;
import com.google.common.collect.Queues;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class ApplicationTest {

  private QueueSupervisor supervisor;
  private ExecutorService executor;
  private Parser parser;
  private Requester requester;
  private Persister persister;

  @Before
  public void setUp() throws Exception {
    supervisor = Mockito.mock(QueueSupervisor.class);
    executor = Mockito.mock(ExecutorService.class);
    parser = Mockito.mock(Parser.class);
    requester = Mockito.mock(Requester.class);
    persister = Mockito.mock(Persister.class);
  }

  @Test
  public void testStart() throws Exception {
    SimpleQueue<String> toBeCrawledQueue = SimpleQueue.create(Queues.newLinkedBlockingQueue());
    when(supervisor.get(QueueId.TO_BE_CRAWLED)).thenReturn(toBeCrawledQueue);

    assertEquals(0, toBeCrawledQueue.size());
    new Application()
        .start("http://valid-url.com", supervisor, executor, parser, requester, persister);
    assertEquals(1, toBeCrawledQueue.size());
  }
}
