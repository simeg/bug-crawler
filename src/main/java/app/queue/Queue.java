package app.queue;

import java.util.concurrent.LinkedBlockingQueue;

public class Queue extends LinkedBlockingQueue<String> {

  /*
   * For each transaction - persist in DB
   * so if application shuts down, it can
   * be restarted in the same exact state
   */
}
