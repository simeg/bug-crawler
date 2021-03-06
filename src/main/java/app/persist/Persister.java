package app.persist;

import app.analyze.Bug;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

import static app.db.PsqlContextHandler.getContext;
import static org.simeg.jooq.web_crawler.Tables.BUG;

public class Persister {

  private static final Logger LOG = LoggerFactory.getLogger(Persister.class);

  private final DSLContext context;

  private Persister(DSLContext context) {
    this.context = context;
  }

  public static Persister create(
      String driverClass,
      String host,
      int port,
      String dbName,
      String username,
      String password) {
    return new Persister(getContext(driverClass, host, port, dbName, username, password));
  }

  public void storeQueueItem(Object item) {
    // Misha:
    // Any object can be serialized to JSON like this (as long as you configure it's class with
    // proper annotations like @JsonProperty and stuff):
    // new ObjectMapper().writeValueAsString(object)
  }

  public boolean storeBug(Bug bug) {
    try {
      LOG.info("Storing bug type [{}] for url: {}", bug.type.toString(), bug.baseUrl);

      this.context.insertInto(
          BUG,
          BUG.TYPE, BUG.BASE_URL, BUG.PATH, BUG.DESCRIPTION, BUG.TIME_INSERTED)
          .values(
              bug.type.name(),                           // Type
              bug.baseUrl,                               // Base URL
              bug.path.orElse(null),                     // Path
              bug.description,                           // Description
              new Timestamp(System.currentTimeMillis())) // Time inserted
          .execute();
    } catch (DataAccessException e) {
      LOG.error("Error storing {} in DB: {}", bug.toString(), e);
      return false;
    }

    return true;
  }

  public void remove(Object item) {
    // TODO
  }
}
