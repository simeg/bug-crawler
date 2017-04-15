package app.queue;

import app.analyze.Bug;
import app.persist.Persister;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class QueueSupervisor {

  private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisor.class);

  public static PersistentQueue<String> subLinkQueue;
  public static PersistentQueue<String> crawledLinkQueue;
  public static PersistentQueue<Bug> bugsQueue;

  private QueueSupervisor(
      PersistentQueue<String> subLinkQueue,
      PersistentQueue<String> crawledLinkQueue,
      PersistentQueue<Bug> bugsQueue) {
    QueueSupervisor.subLinkQueue = subLinkQueue;
    QueueSupervisor.crawledLinkQueue = crawledLinkQueue;
    QueueSupervisor.bugsQueue = bugsQueue;
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

  public boolean addToAnalyze(String url) {
    return crawledLinkQueue.add(url);
  }

  public boolean addToAnalyze(Collection<String> url) {
    return crawledLinkQueue.addAll(url);
  }

  public boolean addToCrawl(String url) {
    return subLinkQueue.add(url);
  }

  public boolean addToCrawl(Collection<String> url) {
    return subLinkQueue.addAll(url);
  }

  public boolean addToPersist(Bug bug) {
    return bugsQueue.add(bug);
  }

  public boolean addToPersist(Collection<Bug> bug) {
    return bugsQueue.addAll(bug);
  }

  // QUESTION:
  // Cleaner way of exposing queues?
  public PersistentQueue<String> subLinks() {
    return subLinkQueue;
  }

  public PersistentQueue<String> crawledLinks() {
    return crawledLinkQueue;
  }

  public PersistentQueue<Bug> bugs() {
    return bugsQueue;
  }
}
