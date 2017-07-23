package app.work;

import app.analyze.Bug;
import app.persist.Persister;
import app.queue.SimpleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class PersisterWorker implements Worker<Bug> {

  private static final Logger LOG = LoggerFactory.getLogger(PersisterWorker.class);

  private final ExecutorService executor;
  private final Persister persister;
  private final SimpleQueue<Bug> queue;

  public PersisterWorker(
      ExecutorService executor,
      Persister persister,
      SimpleQueue<Bug> queue) {
    this.executor = executor;
    this.persister = persister;
    this.queue = queue;
  }

  @Override
  public void start(int threadCount) {
    IntStream.range(0, threadCount).forEach(this::pollQueue);
  }

  private void pollQueue(int threadNumber) {
    executor.submit(() -> {
      try {
        final String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName("Persister-" + threadNumber);
        LOG.info("Started persister thread with name: {}", Thread.currentThread().getName());

        while (true) {
          try {
            final Bug bug = queue.poll(10, TimeUnit.SECONDS);

            if (bug == null) {
              // If there's nothing on the queue ignore it
              continue;
            }

            doWork(bug); // TODO: DRY?

          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Polling was interrupted: {}", e);
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      } catch (Throwable e) {
        LOG.error("PersisterWorker failed", e);
      }
    });
  }

  private void doWork(Bug bug) {
    persister.storeBug(bug);
  }
}
