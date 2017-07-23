package app.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Worker<T> {

  void start(int threadCount);

  class NoopWorker implements Worker<String> {

    private static final Logger LOG = LoggerFactory.getLogger(NoopWorker.class);

    @Override
    public void start(int threadCount) {
      LOG.info("NoopWorker start() called with arg threadCount=[{}]", String.valueOf(threadCount));
    }
  }

}
