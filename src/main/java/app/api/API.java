package app.api;

import app.Application;
import app.analyze.Bug;
import app.analyze.Bug.BugType;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.jooq.util.maven.web_crawler.Tables.BUG;

@Component
public class API {

  private static final Logger LOG = LoggerFactory.getLogger(API.class);

  private final Application app;
  private final DSLContext context;

  @Autowired
  public API(DSLContext context, Application app) {
    this.app = app;
    this.context = context;
  }

  public void runApp(String url) {
    app.init(url);
  }

  public boolean isRunning() {
    return app.isRunning;
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
