package app.analyze;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Analyzer {
  /*
   * Shall detect security bugs for provided URL
   */

  private static final Logger LOG = Logger.getLogger(Analyzer.class.getName());

  public void analyze(String url) {
    LOG.log(Level.INFO, "Will analyze URL: {0}", url);

    // TODO
  }
}
