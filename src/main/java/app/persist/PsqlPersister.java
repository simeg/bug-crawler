package app.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

public class PsqlPersister<T> implements Persister {

  private static final Logger LOG = LoggerFactory.getLogger(PsqlPersister.class);

  private static Connection connection;

  // TODO: Put these in a config
  private static final String HOST = "localhost";
  private static final int PORT = 5432;
  private static final String DB_NAME = "web_crawler";
  private static final String DB_USERNAME = "postgres";
  private static final String DB_PASSWORD = "postgres";

  private PsqlPersister(Connection connection) {
    this.connection = connection;
  }

  public static PsqlPersister create(String driverClass) {
    try {
      // Load the driver
      Class.forName(driverClass);

      Connection connection = DriverManager.getConnection(
          String.format("jdbc:postgresql://%s:%d/%s", HOST, PORT, DB_NAME),
          DB_USERNAME,
          DB_PASSWORD);
      LOG.info("{}: Established connection to DB", Thread.currentThread().getName());

      return new PsqlPersister(connection);

    } catch (ClassNotFoundException e) {
      LOG.error("{}: Class for DB driver not found: {}", Thread.currentThread().getName(), driverClass);
    } catch (SQLException e) {
      LOG.error("{}: Could not connect to DB - is DB started?: {}", Thread.currentThread().getName(), e.toString());
    }

    return null;
  }

  @Override
  public boolean store(Object url) {
    LOG.info(
        "{}: Storing transaction: {}",
        Thread.currentThread().getName(),
        url);

    // TODO

    return true;
  }

  @Override
  public boolean storeAll(Collection urls) {
    LOG.info(
        "{}: Storing transactions: {}",
        Thread.currentThread().getName(),
        urls);

    // TODO

    return true;
  }
}
