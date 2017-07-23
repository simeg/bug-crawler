package app.work;

import app.queue.SimpleQueue;
import app.request.Requester;
import app.request.UrlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import static app.util.RequestUtils.requestType;
import static app.work.QueuePoller.pollQueue;

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
    IntStream.range(0, threadCount).forEach(i ->
        pollQueue("Requester", i, LOG, executor, queue, this::handleRequest)
    );
  }

  @SuppressWarnings("unchecked")
  private void handleRequest(UrlRequest urlRequest) {
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
