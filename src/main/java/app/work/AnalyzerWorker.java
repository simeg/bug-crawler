package app.work;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.parse.Parser;
import app.plugin.*;
import app.queue.QueueId;
import app.queue.QueueSupervisor;
import app.queue.SimpleQueue;
import app.request.Requester;
import app.url.Url;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import static app.work.QueuePoller.pollQueue;

public class AnalyzerWorker implements Worker<String> {

  private static final Logger LOG = LoggerFactory.getLogger(AnalyzerWorker.class);

  private final ExecutorService executor;
  private final Requester requester;
  private final Parser parser;
  private final QueueSupervisor supervisor;
  private final SimpleQueue<Url> queue;

  public AnalyzerWorker(
      ExecutorService executor,
      Requester requester,
      Parser parser,
      QueueSupervisor supervisor,
      SimpleQueue<Url> queue) {
    this.executor = executor;
    this.requester = requester;
    this.parser = parser;
    this.supervisor = supervisor;
    this.queue = queue;
  }

  @Override
  public void start(int threadCount) {
    IntStream.range(0, threadCount).forEach(i ->
        pollQueue("Analyzer", i, LOG, executor, queue, this::analyze)
    );
  }

  private void analyze(Url urlToAnalyze) {
    List<Plugin> plugins = Arrays.asList(
        new HtmlComments(requester, parser),
        new Wordpress(requester),
        new SubPageFinder(requester),
        new PhpInfo(requester)
    );

    Analyzer analyzer = new Analyzer(plugins);
    ImmutableSet<Bug> bugs = analyzer.analyze(urlToAnalyze);

    bugs.forEach(bug -> supervisor.get(QueueId.TO_BE_STORED_AS_BUG).add(bug));
  }

}
