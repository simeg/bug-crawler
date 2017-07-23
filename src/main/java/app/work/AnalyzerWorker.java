package app.work;

import app.analyze.Analyzer;
import app.analyze.Bug;
import app.parse.Parser;
import app.plugin.*;
import app.queue.QueueId;
import app.queue.QueueSupervisor;
import app.queue.SimpleQueue;
import app.request.Requester;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class AnalyzerWorker implements Worker<String> {

  private static final Logger LOG = LoggerFactory.getLogger(AnalyzerWorker.class);

  private final ExecutorService executor;
  private final Requester requester;
  private final Parser parser;
  private final QueueSupervisor supervisor;
  private final SimpleQueue<String> queue;

  public AnalyzerWorker(
      ExecutorService executor,
      Requester requester,
      Parser parser,
      QueueSupervisor supervisor,
      SimpleQueue<String> queue) {
    this.executor = executor;
    this.requester = requester;
    this.parser = parser;
    this.supervisor = supervisor;
    this.queue = queue;
  }

  @Override
  public void start(int threadCount) {
    IntStream.range(0, threadCount).forEach(this::pollQueue);
  }

  private void pollQueue(int threadNumber) {
    executor.submit(() -> {
      try {
        final String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName("Analyzer-" + threadNumber);
        LOG.info("Started analyzer thread with name: {}", Thread.currentThread().getName());

        while (true) {
          try {
            final String url = queue.poll(10, TimeUnit.SECONDS);

            if (url == null) {
              // If there's nothing on the queue ignore it
              continue;
            }

            doWork(url); // TODO: DRY?

          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Polling was interrupted: {}", e);
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      } catch (Throwable e) {
        LOG.error("AnalyzerWorker failed", e);
      }
    });
  }

  private void doWork(String urlToAnalyze) {
    final List<Plugin> plugins = Arrays.asList(
        new HtmlComments(requester, parser),
        new Wordpress(requester),
        new SubPageFinder(requester),
        new PhpInfo(requester)
    );

    final Analyzer analyzer = new Analyzer(plugins);
    final ImmutableSet<Bug> bugs = analyzer.analyze(urlToAnalyze);

    bugs.forEach(bug -> supervisor.get(QueueId.TO_BE_STORED_AS_BUG).add(bug));
  }
}
