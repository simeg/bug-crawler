package app.persist;

import static app.db.PsqlContextHandler.getContext;

import java.util.Collection;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public boolean store(Object object) {
    // Misha:
    // Any object can be serialized to JSON like this (as long as you configure it's class with
    // proper annotations like @JsonProperty and stuff):
    // new ObjectMapper().writeValueAsString(object)
    return false;
  }

  public boolean store(Collection<?> urls) {
    // If at least one element fails to store,
    // return false
    boolean aggregatedResult = true;

    for (Object url : urls) {
      boolean result = store(url);
      if (!result) {
        aggregatedResult = false;
      }
    }

    return aggregatedResult;
  }

}
