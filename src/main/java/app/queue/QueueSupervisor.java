package app.queue;

import app.analyze.Bug;
import app.persist.Persister;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class QueueSupervisor {

  private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisor.class);

  private static PersistentQueue<String> subLinkQueue;
  private static PersistentQueue<String> crawledLinkQueue;
  private static PersistentQueue<Bug> bugsQueue;

  // TODO: Make it possible to only send in pass in one PersistentQueue
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

  /*
   * Adding to queues
   */
  public boolean addToAnalyze(String url) {
    return crawledLinkQueue.add(url);
  }

  public boolean addToAnalyze(Collection<String> url) {
    return crawledLinkQueue.add(url);
  }

  public boolean addToCrawl(String url) {
    return subLinkQueue.add(url);
  }

  public boolean addToCrawl(Collection<String> url) {
    return subLinkQueue.add(url);
  }

  public boolean addToPersist(Bug bug) {
    return bugsQueue.add(bug);
  }

  public boolean addToPersist(Collection<Bug> bug) {
    return bugsQueue.add(bug);
  }

  /*
   * Fetching queues and their data
   */
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

  public static int getSubLinksInQueue() {
    return subLinkQueue.size();
  }

  public static int getCrawledLinksInQueue() {
    return crawledLinkQueue.size();
  }

  public static int getBugsLinksInQueue() {
    return bugsQueue.size();
  }
}
