package app.api;

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

public class CrawlerAPI implements API {

  private static final Logger LOG = LoggerFactory.getLogger(CrawlerAPI.class);

  private final DSLContext context;

  private CrawlerAPI(DSLContext context) {
    this.context = context;
  }

  public static CrawlerAPI create(
      String driverClass,
      String host,
      int port,
      String dbName,
      String username,
      String password) {
    return new CrawlerAPI(getContext(driverClass, LOG, host, port, dbName, username, password));
  }

  @Override
  public List<Bug> getAllBugs() {
    return this.context.select()
        .from(BUG)
        .fetch()
        .map(this::toBug);
  }

  @Override
  public List<Bug> getBugs(String url) {
    return context.selectFrom(BUG)
        .where(BUG.BASE_URL.equal(url))
        .fetch()
        .map(this::toBug);
  }

  private Bug toBug(Record record) {
    return Bug.create(
        BugType.valueOf(record.get(BUG.TYPE)),
        record.get(BUG.BASE_URL),
        record.get(BUG.DESCRIPTION),
        Optional.of(record.get("path").toString())
    );
  }
}
