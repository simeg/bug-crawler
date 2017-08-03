package app;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

public class SystemTest {

  private static final Logger LOG = LoggerFactory.getLogger(SystemTest.class);

//  @Rule
//  public NginxContainer nginx = new NginxContainer();


  @Before
  public void setUp() throws Exception {
    PostgreSQLContainer database = new PostgreSQLContainer()
        .withDatabaseName("web_crawler")
        .withUsername("postgres")
        .withPassword("");

    database.start();

    GenericContainer app =
        new GenericContainer("simeg/web-crawler:latest")
            .withEnv("DATABASE_HOST", getHost(database))
            .withEnv("DATABASE_PORT", String.valueOf(database.getMappedPort(5432)));

    app.followOutput(new Slf4jLogConsumer(LOG));
    app.start();
  }

  @Test
  public void testSystem() throws Exception {
    System.out.println("hello");
  }

  private static String getHost(PostgreSQLContainer database) {
    return database.getContainerInfo().getNetworkSettings()
        .getNetworks().get("bridge").getGateway();
  }
}
