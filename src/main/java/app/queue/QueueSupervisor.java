package app.queue;

import app.analyze.Bug;
import app.persist.Persister;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class QueueSupervisor {

  private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisor.class);

  private static PersistentQueue<String> subLinkQueue;
  private static PersistentQueue<String> crawledLinkQueue;
  private static PersistentQueue<Bug> bugsQueue;

  private final Set<String> analyzedLinksCache;
  private final Set<String> crawledLinksCache;
  private final int CACHE_INITIAL_CAPACITY = 100000;

  // TODO: Make it possible to only send in pass in one PersistentQueue
  private QueueSupervisor(
      PersistentQueue<String> subLinkQueue,
      PersistentQueue<String> crawledLinkQueue,
      PersistentQueue<Bug> bugsQueue) {
    QueueSupervisor.subLinkQueue = subLinkQueue;
    QueueSupervisor.crawledLinkQueue = crawledLinkQueue;
    QueueSupervisor.bugsQueue = bugsQueue;
    analyzedLinksCache = new HashSet<>(CACHE_INITIAL_CAPACITY);
    crawledLinksCache = new HashSet<>(CACHE_INITIAL_CAPACITY);
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
  public boolean addToCrawl(String url) {
    return isUnique(crawledLinksCache, url) && subLinkQueue.add(url);
  }

  public boolean addToCrawl(Collection<String> urls) {
    final Set<String> uniqueUrls = getUniqueElements(crawledLinksCache, urls);
    return subLinkQueue.add(uniqueUrls);
  }

  public boolean addToAnalyze(String url) {
    return isUnique(analyzedLinksCache, url) && crawledLinkQueue.add(url);
  }

  public boolean addToAnalyze(Collection<String> urls) {
    final Set<String> uniqueUrls = getUniqueElements(analyzedLinksCache, urls);
    return crawledLinkQueue.add(uniqueUrls);
  }

  public boolean addToPersist(Bug bug) {
    return bugsQueue.add(bug);
  }

  public boolean addToPersist(Collection<Bug> bugs) {
    return bugsQueue.add(bugs);
  }

  <T> boolean isUnique(Set<T> cache, T url) {
    final boolean isUnique = cache.add(url);
    if (!isUnique) {
      LOG.info("{}: Skipping duplicate url: {}", Thread.currentThread().getName(), url);
      return false;
    }
    return true;
  }

  <T> Set<T> getUniqueElements(Set<T> cache, Collection<T> urls) {
    final Set<T> uniqueUrls = new HashSet<>(CACHE_INITIAL_CAPACITY);

    uniqueUrls.addAll(
        urls.stream()
            .filter(url -> {
              final boolean isUnique = cache.add(url);
              if (isUnique) {
                return true;
              } else {
                LOG.info("{}: Skipping duplicate url: {}", Thread.currentThread().getName(), url);
                return false;
              }
            })
            .collect(Collectors.toSet()));

    return uniqueUrls;
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
