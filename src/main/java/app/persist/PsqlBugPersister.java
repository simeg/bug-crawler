package app.persist;

import app.analyze.Bug;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

public class PsqlBugPersister implements Persister<Bug> {

  private static final Logger LOG = LoggerFactory.getLogger(PsqlBugPersister.class);

  private final Connection connection;
  private final DSLContext context;

  // TODO: Put these in a config
  private static final String HOST = "localhost";
  private static final int PORT = 5432;
  private static final String DB_NAME = "web_crawler";
  private static final String DB_USERNAME = "postgres";
  private static final String DB_PASSWORD = "postgres";

  private PsqlBugPersister(Connection connection, DSLContext context) {
    this.connection = connection;
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
      LOG.info("{}: Established connection to DB", Thread.currentThread().getName());

      DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);

      return new PsqlBugPersister(connection, context);

    } catch (ClassNotFoundException e) {
      LOG.error("{}: Class for DB driver not found: {}", Thread.currentThread().getName(), driverClass);
    } catch (SQLException e) {
      LOG.error("{}: Could not connect to DB - is DB started?: {}", Thread.currentThread().getName(), e.toString());
    }

    return null;
  }

  @Override
  public boolean store(Bug bug) {
    LOG.info(
        "{}: Storing bug: {}",
        Thread.currentThread().getName(),
        bug.toString());

    // TODO

    return true;
  }

  @Override
  public boolean storeAll(Collection<Bug> bugs) {
    LOG.info(
        "{}: Storing bugs: {}",
        Thread.currentThread().getName(),
        bugs);

    // TODO

    return true;
  }
}
