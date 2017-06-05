package app.queue;

import app.persist.Persister;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueSupervisor {

  private static final Logger LOG = LoggerFactory.getLogger(QueueSupervisor.class);

  private final ImmutableMap<QueueId, SimpleQueue<?>> queues;

  QueueSupervisor(ImmutableMap<QueueId, SimpleQueue<?>> queues) {
    this.queues = queues;
  }

  public static QueueSupervisor create(Persister persister) {
    return new QueueSupervisor(
        QueueId.Registry.getAll()
            .stream()
            .collect(ImmutableMap.toImmutableMap(
                id -> id,
                id -> createQueue(persister))));
  }

  static SimpleQueue<?> createQueue(Persister persister) {
    return new PersistentQueue<>(
        persister,
        SimpleQueue.create(Queues.newLinkedBlockingQueue()));
  }

  @SuppressWarnings("unchecked")
  public <T> SimpleQueue<T> get(QueueId<T> id) {
    return (SimpleQueue<T>) queues.get(id);
  }
}
