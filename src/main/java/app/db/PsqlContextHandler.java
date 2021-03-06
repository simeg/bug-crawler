package app.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PsqlContextHandler {

  private static final Logger LOG = LoggerFactory.getLogger(PsqlContextHandler.class);

  public static DSLContext getContext(
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

/*
      try (PreparedStatement statement = connection.prepareStatement("INSERT ? ")) {
      statement.execute();
      }

      try (PreparedStatement statement = connection.prepareStatement("SELECT FROM ... WHERE ... = ? ")) {
        statement.setString(0, "");
        try (ResultSet resultSet = statement.getResultSet()) {
          while (resultSet.next()) {
            final String data = resultSet.getString("data");
          }
        }
      }
*/
      Settings settings = new Settings()
          .withRenderFormatted(false) // Pretty print
          .withExecuteLogging(false); // Disable default logging by jOOQ
      DSLContext context = DSL.using(connection, SQLDialect.POSTGRES, settings);

      LOG.info("Established connection to DB");

      return context;

    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed find database driver", e);

    } catch (SQLException e) {
      throw new RuntimeException("Could not connect to DB - is DB started?", e);
    }
  }
}
