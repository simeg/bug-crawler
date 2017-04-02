package app.queue;

import java.util.LinkedList;

public class Queue extends LinkedList<String> {

  /*
   * For each transaction - persist in DB
   * so if application shuts down, it can
   * be restarted in the same exact state
   */
}
