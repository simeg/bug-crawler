package app;

import org.junit.Test;

public class ApplicationTest {

  @Test
  public void testAppStartsWithoutExceptions() throws Exception {
    // Just to see that application can be started without any exceptions
    Application app = new Application();
    final String workingWebsite = "http://www.vecka.nu";
    app.start(workingWebsite);
  }
}
