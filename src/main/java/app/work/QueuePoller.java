package app.work;

import app.queue.SimpleQueue;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

final class QueuePoller {

  static <T> void pollQueue(
      String name,
      int threadNumber,
      Logger logger,
      ExecutorService executor,
      SimpleQueue<T> queue,
      Consumer<T> jobToDo) {
    executor.submit(() -> {
      try {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(String.format("%s-%s", name, threadNumber));
        logger.info("Started {} thread with name: {}",
            name.toLowerCase(),
            Thread.currentThread().getName());

        while (true) {
          try {
            T item = queue.poll(10, TimeUnit.SECONDS);

            if (item == null) {
              // If there's nothing on the queue ignore it
              continue;
            }

            jobToDo.accept(item);

          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Polling was interrupted: {}", e);
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      } catch (Throwable e) {
        logger.error(String.format("%sWorker failed", name), e);
      }
    });
  }

}
