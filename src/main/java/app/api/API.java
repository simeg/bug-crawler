package app.api;

import app.Application;
import app.analyze.Bug;
import app.analyze.Bug.BugType;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static app.db.PsqlContextHandler.getContext;
import static org.jooq.util.maven.web_crawler.Tables.BUG;

public class API {

  private static final Logger LOG = LoggerFactory.getLogger(API.class);

  private final DSLContext context;

  public API(DSLContext context) {
    this.context = context;
  }

  public static API create(
      String driverClass,
      String host,
      int port,
      String dbName,
      String username,
      String password) {
    return new API(getContext(driverClass, host, port, dbName, username, password));
  }

  public void runApp(String url) {
    new Application().init(url);
  }

  public List<Bug> getAllBugs() {
    return this.context.select()
        .from(BUG)
        .fetch()
        .map(this::toBug);
  }

  public List<Bug> getBugs(String url) {
    return context.selectFrom(BUG)
        .where(BUG.BASE_URL.equal(url))
        .fetch()
        .map(this::toBug);
  }

  private Bug toBug(Record record) {
    return new Bug(
        BugType.valueOf(record.get(BUG.TYPE)),
        record.get(BUG.BASE_URL),
        record.get(BUG.DESCRIPTION),
        Optional.of(record.get("path").toString())
    );
  }
}
