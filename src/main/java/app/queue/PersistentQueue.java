package app.queue;

import app.persist.Persister;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class PersistentQueue<T> {
  /*
   * For each transaction - persist in DB
   * so if application shuts down, it can
   * be restarted in the same exact state
   */

  private final BlockingQueue<T> queue;
  private final Persister<T> persister;

  private PersistentQueue(BlockingQueue<T> queue, Persister<T> persister) {
    this.queue = queue;
    this.persister = persister;
  }

  public static <T> PersistentQueue<T> create(BlockingQueue<T> queue, Persister<T> persister) {
    return new PersistentQueue<>(queue, persister);
  }

  public boolean add(T element) {
    this.persister.store(element);
    return this.queue.add(element);
  }

  public boolean addAll(Collection<? extends T> elements) {
    this.persister.storeAll(elements);
    return this.queue.addAll(elements);
  }

  public T poll(long timeout, TimeUnit unit) throws InterruptedException {
    T element = this.queue.poll(timeout, unit);

    this.persister.store(element);
    return element;
  }
}
