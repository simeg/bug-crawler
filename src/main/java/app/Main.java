package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Main {

  // TODO: Setup Sentry
  // TODO: SubPageFinder is reporting bugs even though status other than 200

  public static void main(String[] args) throws IOException, InterruptedException {
    SpringApplication.run(Main.class, args);
  }

}
