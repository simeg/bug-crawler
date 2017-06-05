package app.queue;

import app.analyze.Bug;
import app.request.UrlRequest;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;

public interface QueueId<T> {

  QueueId<Bug> BUG = Registry.create();
  QueueId<String> SUBLINK = Registry.create();
  QueueId<String> CRAWLED = Registry.create();
  QueueId<UrlRequest> REQUEST = Registry.create();

  class Registry {

    private static Set<QueueId> ALL_IDS = Sets.newHashSet();

    private static <T> QueueId<T> create() {
      final QueueId<T> id = new QueueId<T>() {
      };
      ALL_IDS.add(id);
      return id;
    }

    static ImmutableSet<QueueId> getAll() {
      return ImmutableSet.copyOf(ALL_IDS);
    }
  }
}
