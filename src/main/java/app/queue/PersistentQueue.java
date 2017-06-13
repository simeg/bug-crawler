package app.queue;

import app.persist.Persister;

import java.util.concurrent.TimeUnit;

public class PersistentQueue<T> implements SimpleQueue<T> {

  private final Persister persister;
  private final SimpleQueue<T> queue;

  public PersistentQueue(Persister persister, SimpleQueue<T> queue) {
    this.persister = persister;
    this.queue = queue;
  }

  @Override
  public void add(T item) {
    persister.storeQueueItem(item);
    queue.add(item);
  }

  @Override
  public T poll(long timeout, TimeUnit unit) throws InterruptedException {
    T item = queue.poll(timeout, unit);
    persister.remove(item);
    return item;
  }

  @Override
  public int size() {
    return queue.size();
  }
}
