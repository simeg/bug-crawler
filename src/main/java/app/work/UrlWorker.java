package app.work;

import app.queue.SimpleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public final class UrlWorker<T> implements Worker {

  private static final Logger LOG = LoggerFactory.getLogger(UrlWorker.class);

  private final String name;
  private final ExecutorService executor;
  private final SimpleQueue<T> queue;
  private final Consumer<T> jobToDo;

  public UrlWorker(
      String name,
      ExecutorService executor,
      SimpleQueue<T> queue,
      Consumer<T> jobToDo) {
    this.name = name;
    this.executor = executor;
    this.queue = queue;
    this.jobToDo = jobToDo;
  }

  public void start(int threadCount) {
    IntStream.range(0, threadCount).forEach(this::pollQueue);
  }

  private void pollQueue(int threadNumber) {
    executor.submit(() -> {
      try {
        final String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(name + "-" + threadNumber);
        LOG.info("Started {} thread with name: {}",
            name.toLowerCase(),
            Thread.currentThread().getName());

        while (true) {
          try {
            final T url = queue.poll(10, TimeUnit.SECONDS);

            if (url == null) {
              // If there's nothing on the queue ignore it
              continue;
            }

            jobToDo.accept(url);

          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Polling was interrupted: {}", e);
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      } catch (Throwable e) {
        throw new RuntimeException("UrlWorker failed for some reason", e);
      }
    });
  }

}
