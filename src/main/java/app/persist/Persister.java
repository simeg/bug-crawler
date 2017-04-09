package app.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class Persister<T> {

  private static final Logger LOG = LoggerFactory.getLogger(Persister.class);
  // TODO: Change to some PostgreSQL class. Or even more abstract?
  private static Class database;

  private Persister() {
    this.database = database;
  }

  public static Persister create() {
    return new Persister();
  }

  public boolean store(T url) {
    LOG.info(
        "{}: Storing transaction: {}",
        Thread.currentThread().getName(),
        url);

    // TODO

    return true;
  }

  public boolean storeAll(Collection<? extends T> urls) {
    LOG.info(
        "{}: Storing transaction: {}",
        Thread.currentThread().getName(),
        urls);

    // TODO

    return true;
  }
}
