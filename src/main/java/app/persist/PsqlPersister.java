package app.persist;

import app.analyze.Bug;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import static org.jooq.util.maven.example.Tables.BUG;

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
  public boolean store(T url) {
    // TODO
    return false;
  }

  @Override
  public boolean storeAll(Collection<T> urls) {
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

  @Override
  public boolean storeAllBugs(Collection<Bug> bugs) {
    return this.storeAll((Collection<T>) bugs);
  }

}
