package app.work;

import app.analyze.Bug;
import app.persist.Persister;
import app.queue.SimpleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import static app.work.QueuePoller.pollQueue;

public class PersisterWorker implements Worker<Bug> {

  private static final Logger LOG = LoggerFactory.getLogger(PersisterWorker.class);

  private final ExecutorService executor;
  private final Persister persister;
  private final SimpleQueue<Bug> queue;

  public PersisterWorker(ExecutorService executor, Persister persister, SimpleQueue<Bug> queue) {
    this.executor = executor;
    this.persister = persister;
    this.queue = queue;
  }

  @Override
  public void start(int threadCount) {
    IntStream.range(0, threadCount).forEach(i ->
        pollQueue("Persister", i, LOG, executor, queue, persister::storeBug)
    );
  }

}
