package app;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  // TODO: Setup Sentry
  // TODO: SubPageFinder is reporting bugs even though status other than 200

  public static void main(String[] args) throws IOException, InterruptedException {

    // To support Spring + testing we could make a POST request from the test to get the website
    // into the "form"
    SpringApplication.run(Main.class, args);

    /*LOG.info("args: " + Arrays.toString(args));

    final String url = System.getenv("URL");
    new Application().init(url);*/
  }

}
