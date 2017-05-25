package app.request;

import java.util.concurrent.CompletableFuture;

public interface Requester {

  CompletableFuture get(String url);

  Object request(String url);

}
