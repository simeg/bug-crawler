package app.queue;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CachingQueue<T> implements SimpleQueue<T> {

  private final SimpleQueue<T> queue;

  private final Set<T> cache = Sets.newHashSet();

  public CachingQueue(SimpleQueue<T> queue) {
    this.queue = queue;
  }

  @Override
  public void add(T item) {
    if (cache.add(item)) {
      queue.add(item);
    }
  }

  @Override
  public T poll(long timeout, TimeUnit unit) throws InterruptedException {
    return queue.poll(timeout, unit);
  }

  @Override
  public int size() {
    return queue.size();
  }
}
