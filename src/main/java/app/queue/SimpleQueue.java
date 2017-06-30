package app.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface SimpleQueue<T> {

  void add(T element);

  T poll(long timeout, TimeUnit unit) throws InterruptedException;

  int size();

  static <T> SimpleQueue<T> create(BlockingQueue<T> queue) {
    return new SimpleQueue<T>() {
      @Override
      public void add(T item) {
        queue.add(item);
      }

      @Override
      public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
      }

      @Override
      public int size() {
        return queue.size();
      }
    };
  }
}
