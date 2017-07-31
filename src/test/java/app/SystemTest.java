package app;

import org.junit.Before;
import org.junit.Rule;
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

  @Rule
  public PostgreSQLContainer psql = new PostgreSQLContainer()
      .withDatabaseName("web_crawler")
      .withPassword("postgres")
      .withUsername("postgres");

  @Rule
  public GenericContainer container =
      new GenericContainer("simeg/web-crawler:latest");

  @Before
  public void setUp() throws Exception {
    Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOG);
    container.followOutput(logConsumer);
  }

  @Test
  public void testSystem() throws Exception {
    psql.start();
    container.start();
  }
}
