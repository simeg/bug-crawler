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

import static org.jooq.util.maven.example.tables.Bug.BUG;

public class PsqlBugPersister implements Persister<Bug> {

  private static final Logger LOG = LoggerFactory.getLogger(PsqlBugPersister.class);

  private final DSLContext context;

  // TODO: Put these in a config
  private static final String HOST = "localhost";
  private static final int PORT = 5432;
  private static final String DB_NAME = "web_crawler";
  private static final String DB_USERNAME = "postgres";
  private static final String DB_PASSWORD = "postgres";

  private PsqlBugPersister(DSLContext context) {
    this.context = context;
  }

  public static PsqlBugPersister create(String driverClass) {
    try {
      // Load the driver
      Class.forName(driverClass);

      Connection connection = DriverManager.getConnection(
          String.format("jdbc:postgresql://%s:%d/%s", HOST, PORT, DB_NAME),
          DB_USERNAME,
          DB_PASSWORD);
      DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);

      LOG.info("{}: Established connection to DB", Thread.currentThread().getName());

      return new PsqlBugPersister(context);

    } catch (ClassNotFoundException e) {
      LOG.error("{}: Class for DB driver not found: {}", Thread.currentThread().getName(), driverClass);
    } catch (SQLException e) {
      LOG.error("{}: Could not connect to DB - is DB started?: {}", Thread.currentThread().getName(), e.toString());
    }

    return null;
  }

  @Override
  public boolean store(Bug bug) {
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
  public boolean storeAll(Collection<Bug> bugs) {
    // If at least one bug failed to be stored,
    // return false
    boolean aggregatedResult = true;

    for (Bug bug : bugs) {
      boolean result = store(bug);
      if (!result) {
        aggregatedResult = false;
      }
    }

    return aggregatedResult;
  }
}
