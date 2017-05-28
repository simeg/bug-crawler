package app.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PsqlContextHandler {

  public static DSLContext getContext(
      String driverClass,
      Logger logger,
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

      logger.info("Established connection to DB");

      return context;

    } catch (ClassNotFoundException e) {
      logger.error("Class for DB driver not found: {}", driverClass);
      // QUESTION:
      // More graceful way of shutting the entire application down?
      //System.exit(1);
    } catch (SQLException e) {
      logger.error("Could not connect to DB - is DB started?: {}", e.toString());
      //System.exit(1);
    }

    return null;
  }
}
