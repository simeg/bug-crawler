package app.work;

import app.queue.SimpleQueue;
import app.request.Requester;
import app.request.UrlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static app.util.RequestUtils.requestType;

public class RequesterWorker implements Worker<UrlRequest> {

  private static final Logger LOG = LoggerFactory.getLogger(RequesterWorker.class);

  private final ExecutorService executor;
  private final Requester requester;
  private final SimpleQueue<UrlRequest> queue;

  public RequesterWorker(
      ExecutorService executor,
      Requester requester,
      SimpleQueue<UrlRequest> queue) {
    this.executor = executor;
    this.requester = requester;
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
        Thread.currentThread().setName("Requester-" + threadNumber);
        LOG.info("Started requester thread with name: {}", Thread.currentThread().getName());

        while (true) {
          try {
            final UrlRequest urlRequest = queue.poll(10, TimeUnit.SECONDS);

            if (urlRequest == null) {
              // If there's nothing on the queue ignore it
              continue;
            }

            doWork(urlRequest); // TODO: DRY?

          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.warn("Polling was interrupted: {}", e);
            break;
          }
        }

        Thread.currentThread().setName(oldName);
      } catch (Throwable e) {
        LOG.error("RequesterWorker failed", e);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private void doWork(UrlRequest urlRequest) {
    final Optional<?> requestValue = requestType(this.requester, urlRequest);

    if (requestValue.isPresent()) {
      urlRequest.future.complete(requestValue.get());
    } else {
      urlRequest.future.completeExceptionally(
          new RuntimeException(
              "Future did not succeed, most likely because the response was a 404")
      );
    }
  }
}
