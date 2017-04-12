package app.persist;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

public class PsqlPersister<T> implements Persister<T> {

  private static final Logger LOG = LoggerFactory.getLogger(PsqlPersister.class);

  private final DSLContext context;

  private PsqlPersister(DSLContext context) {
    this.context = context;
  }

  public static PsqlPersister create(
      String driverClass,
      String host,
      int port,
      String dbName,
      String username,
      String password) {
    try {
      // Load the driver
      Class.forName(driverClass);

      Connection connection = DriverManager.getConnection(
          String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName),
          username,
          password);
      DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);

      LOG.info("{}: Established connection to DB", Thread.currentThread().getName());

      return new PsqlPersister(context);

    } catch (ClassNotFoundException e) {
      LOG.error("{}: Class for DB driver not found: {}", Thread.currentThread().getName(), driverClass);
    } catch (SQLException e) {
      LOG.error("{}: Could not connect to DB - is DB started?: {}", Thread.currentThread().getName(), e.toString());
    }

    return null;
  }

  @Override
  public boolean store(T element) {
    try {
      LOG.info(
          "{}: Storing element: {}",
          Thread.currentThread().getName(),
          element.toString());

      // TODO: Figure this out
      /*this.context.insertInto(BUG,
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
    }*/
    } finally {
      // Nothing
    }

    return true;
  }

  @Override
  public boolean storeAll(Collection<T> elements) {
    // If at least one bug failed to be stored,
    // return false
    boolean aggregatedResult = true;

    for (T element : elements) {
      boolean result = store(element);
      if (!result) {
        aggregatedResult = false;
      }
    }

    return aggregatedResult;
  }
}
