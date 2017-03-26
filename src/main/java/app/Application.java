package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.*;

@SpringBootApplication
@RestController
public class Application {

  @RequestMapping("/")
  public String home() {
    return "Hello Docker World";
  }

  public static void main(String[] args) {
    Application app = new Application();
    app.start();
    SpringApplication.run(app.getClass(), args);
  }

  private void start() {
    ScheduledExecutorService scheduledExecutorService =
        Executors.newScheduledThreadPool(1);
    ScheduledFuture scheduledFuture =
        scheduledExecutorService.scheduleWithFixedDelay(
            // Can be Runnable or lambda. Can lambda replace any class?
            () -> {
              try {
                readFromUrl("http://vecka.nu/");
              } catch (IOException e) {
                e.printStackTrace();
              }
            },
            0,
            5,
            TimeUnit.SECONDS
        );
  }

  private void readFromUrl(String urlString) throws IOException {
    URL url = new URL(urlString);
    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

    try {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      reader.close();

    } catch (Exception e) {
      throw e;
    }
  }

}
