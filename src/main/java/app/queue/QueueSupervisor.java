package app.queue;

import app.analyze.Bug;
import app.persist.Persister;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueSupervisor {

  private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisor.class);

  private final PersistentQueue<String> subLinkQueue;
  private final PersistentQueue<String> crawledLinkQueue;
  private final PersistentQueue<Bug> bugsQueue;

  private QueueSupervisor(
      PersistentQueue<String> subLinkQueue,
      PersistentQueue<String> crawledLinkQueue,
      PersistentQueue<Bug> bugsQueue) {
    this.subLinkQueue = subLinkQueue;
    this.crawledLinkQueue = crawledLinkQueue;
    this.bugsQueue = bugsQueue;
  }

  public static QueueSupervisor create(Persister<Bug> bugPersister, Persister<String> persister) {
    final PersistentQueue<String> subLinkQueue =
        PersistentQueue.create(Queues.newLinkedBlockingQueue(), persister);
    final PersistentQueue<String> crawledLinkQueue =
        PersistentQueue.create(Queues.newLinkedBlockingQueue(), persister);
    final PersistentQueue<Bug> bugsQueue =
        PersistentQueue.create(Queues.newLinkedBlockingQueue(), bugPersister);

    return new QueueSupervisor(subLinkQueue, crawledLinkQueue, bugsQueue);
  }

  // QUESTION:
  // Cleaner way of exposing queues?
  public PersistentQueue<String> subLinks() {
    return this.subLinkQueue;
  }

  public PersistentQueue<String> crawledLinks() {
    return this.crawledLinkQueue;
  }

  public PersistentQueue<Bug> bugs() {
    return this.bugsQueue;
  }
}
