package app;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

  // TODO: Setup Sentry
  // TODO: SubPageFinder is reporting bugs even though status other than 200

  public static void main(String[] args) throws IOException, InterruptedException {
    SpringApplication.run(Main.class, args);
  }

}
