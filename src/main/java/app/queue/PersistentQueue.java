package app.queue;

import app.persist.Persister;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PersistentQueue<T> {

  /*
   * For each transaction - persist in DB
   * so if application shuts down, it can
   * be restarted in the same exact state
   */

  private final Queue<T> queue;
  private final Persister persister;

  private PersistentQueue(Queue<T> queue, Persister persister) {
    this.queue = queue;
    this.persister = persister;
  }

  public static <T> PersistentQueue<T> create(Queue<T> queue, Persister persister) {
    return new PersistentQueue<>(queue, persister);
  }

  public boolean add(T url) {
    this.persister.store(url);
    return this.queue.add(url);
  }

  public boolean addAll(Set urls) {
    this.persister.store(urls);
    return this.queue.addAll(urls);
  }

  public T poll(long time, TimeUnit unit) throws InterruptedException {
    // TODO: Implement waiting time?
    T element = this.queue.poll();

    if (element == null) {
      // TODO: Don't do this..
      throw new InterruptedException();
    }

    this.persister.store(element);
    return this.queue.poll();
  }
}
