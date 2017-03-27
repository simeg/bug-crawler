package app;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Routes {

  @RequestMapping("/")
  public String home() {
    return "Home url";
  }

  @RequestMapping("/custom")
  public String doesNotMatterForNow() {
    return "Custom URL";
  }
}
