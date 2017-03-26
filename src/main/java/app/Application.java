package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@SpringBootApplication
@RestController
public class Application {

  private ScheduledExecutorService scheduler;

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
            new Runnable() {
              public void run() {
                System.out.println("WORKING");
              }
            },
            0,
            1,
            TimeUnit.SECONDS
        );
  }

}
