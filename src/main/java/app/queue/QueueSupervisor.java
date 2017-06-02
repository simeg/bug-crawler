package app.queue;

import app.analyze.Bug;
import app.request.UrlRequest;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Queue;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class QueueSupervisor {

  private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisor.class);

  private static PersistentQueue<String> subLinkQueue;
  private static PersistentQueue<String> crawledLinkQueue;
  private static PersistentQueue<Bug> bugsQueue;
  private static PersistentQueue<UrlRequest> requestQueue;

  private final Set<String> analyzedLinksCache;
  private final Set<String> crawledLinksCache;

  private final ImmutableMap<QueueId, PersistentQueue> queues;

  // TODO: Make it possible to only send in pass in one PersistentQueue
  public QueueSupervisor(QueueId... ids) {

    // Misha: TODO: create a queue for each queue id
    for (QueueId id : ids) {
    }
    queues = ImmutableMap.of();

    analyzedLinksCache = Sets.newHashSet();
    crawledLinksCache = Sets.newHashSet();
  }

  public <T> PersistentQueue<T> get(QueueId<T> id) {
    return (PersistentQueue<T>) queues.get(id);
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
//      LOG.info("Skipping duplicate url: {}", url);
      return false;
    }
    return true;
  }

  <T> Set<T> getUniqueElements(Set<T> cache, Collection<T> urls) {
    final Set<T> uniqueUrls = Sets.newHashSet();

    uniqueUrls.addAll(
        urls.stream()
            .filter(url -> {
              final boolean isUnique = cache.add(url);
              if (isUnique) {
                return true;
              } else {
//                LOG.info("Skipping duplicate url: {}", url);
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
  // Cleaner way of exposing queues? Yes - Dependency injection
  public PersistentQueue<String> subLinks() {
    return subLinkQueue;
  }

  public PersistentQueue<String> crawledLinks() {
    return crawledLinkQueue;
  }

  public PersistentQueue<Bug> bugs() {
    return bugsQueue;
  }

  public PersistentQueue<UrlRequest> requests() {
    return requestQueue;
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

  public static class QueueId<T> {
  }
}
