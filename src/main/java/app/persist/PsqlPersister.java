package app.persist;

import app.analyze.Bug;
import app.db.PsqlHandler;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Collection;

import static org.jooq.util.maven.example.Tables.BUG;

public class PsqlPersister<T> extends PsqlHandler implements Persister<T> {

  private static final Logger LOG = LoggerFactory.getLogger(PsqlPersister.class);

  private final DSLContext context;

  private PsqlPersister(DSLContext context) {
    this.context = context;
  }

  public static <T> PsqlPersister<T> create(
      String driverClass,
      String host,
      int port,
      String dbName,
      String username,
      String password) {
    return new PsqlPersister<>(getContext(driverClass, LOG, host, port, dbName, username, password));
  }

  @Override
  public boolean storeBug(Bug bug) {
    try {
      LOG.info(
          "{}: Storing bug: {}",
          Thread.currentThread().getName(),
          bug.toString());

      this.context.insertInto(BUG,
          BUG.TYPE, BUG.URL, BUG.PATH, BUG.DESCRIPTION, BUG.DATE_ADDED)
          .values(
              bug.type.name(),                          // Type
              bug.url,                                  // URL
              bug.path.orElse(null),                    // Path
              bug.description,                          // Description
              new Timestamp(System.currentTimeMillis()) // Date added
          ).execute();
    } catch (DataAccessException e) {
      LOG.error("{}: Error storing {} in DB: {}", Thread.currentThread().getName(), bug.toString(), e.toString());
      return false;
    }

    return true;
  }

  @Override
  public boolean storeBugs(Collection<Bug> bugs) {
    return this.store((Collection<T>) bugs);
  }

  @Override
  public boolean store(T url) {
    // TODO
    return false;
  }

  @Override
  public boolean store(Collection<T> urls) {
    // If at least one element fails to store,
    // return false
    boolean aggregatedResult = true;

    for (T url : urls) {
      boolean result = store(url);
      if (!result) {
        aggregatedResult = false;
      }
    }

    return aggregatedResult;
  }

}
