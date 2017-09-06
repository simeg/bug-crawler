package app;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.NginxContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class SystemTest {

  private static final Logger LOG = LoggerFactory.getLogger(SystemTest.class);

  private static PostgreSQLContainer getDatabaseContainer() {
    return new PostgreSQLContainer()
        .withDatabaseName("web_crawler")
        .withUsername("postgres")
        .withPassword("");
  }

  private static String getHost(GenericContainer container) {
    return container.getContainerInfo().getNetworkSettings()
        .getNetworks().get("bridge").getGateway();
  }

  @Before
  public void setUp() throws Exception {
    PostgreSQLContainer database = getDatabaseContainer();
    database.start();

    final NginxContainer nginx = startNginx();

    final String host = getHost(nginx);
    final Integer port = nginx.getMappedPort(80);

    final String url = "http://" + host + ":" + port;

    LOG.info(url);

    GenericContainer app =
        new GenericContainer("simeg/web-crawler:latest")
            .withEnv("DATABASE_HOST", getHost(database))
            .withEnv("DATABASE_PORT", String.valueOf(database.getMappedPort(5432)))
            .withCommand("test")
            .withEnv("URL", url);

    app.start();
    app.followOutput(new Slf4jLogConsumer(LOG));


  }

  @Test
  public void testSystem() throws Exception {
    System.out.println("hello");
  }

  private NginxContainer startNginx() throws Exception {
    File contentFolder = getContentFolder();

    NginxContainer nginx = new NginxContainer();
    nginx.setCustomContent(contentFolder.toString());
    nginx.start();

    return nginx;
  }

  private File getContentFolder() {
    try {

      File contentFolder = new File("target/.tmp-test-container");
      contentFolder.mkdir();
      contentFolder.setReadable(true, false);
      contentFolder.setWritable(true, false);
      contentFolder.setExecutable(true, false);

      File indexFile = new File(contentFolder, "index.html");
      indexFile.setReadable(true, false);
      indexFile.setWritable(true, false);
      indexFile.setExecutable(true, false);

      PrintStream printStream = new PrintStream(new FileOutputStream(indexFile));
      printStream
          .println("<html><body><a href=\"https://www.svt.se/nyheter/\">Link1</a><a href=\"https://www.svt2.se/nyheter/\">Link2</a></body></html>");

      return contentFolder;

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }
}
