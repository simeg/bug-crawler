package app.queue;

import app.analyze.Bug;
import app.request.UrlRequest;
import app.url.Url;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

public interface QueueId<T> {

  QueueId<Bug> TO_BE_STORED_AS_BUG = Registry.create();
  QueueId<Url> TO_BE_CRAWLED = Registry.create();
  QueueId<Url> TO_BE_ANALYZED = Registry.create();
  QueueId<UrlRequest> TO_BE_REQUESTED = Registry.create();

  static void initialize() {
    // Dummy method to be called to force static initialization of this class
  }

  class Registry {

    private static Set<QueueId> ALL_IDS = Sets.newHashSet();

    private static <T> QueueId<T> create() {
      final QueueId<T> id = new QueueId<T>() {
      };
      ALL_IDS.add(id);
      return id;
    }

    static ImmutableSet<QueueId> getAll() {
      QueueId.initialize();
      return ImmutableSet.copyOf(ALL_IDS);
    }
  }
}
