package app.ui;

import app.api.API;
import app.db.PsqlContextHandler;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@EnableAutoConfiguration
public class ViewController {

  private final API api;

  public ViewController() {
    final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
    api = context.getBean(API.class);
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String indexForm(Model model) {
    model.addAttribute("options", new Options());
    return "index";
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public String formSubmit(@ModelAttribute Options options) {
    return "running";
  }

  public static class Options {

    private String website;

    public String getWebsite() {
      return website;
    }

    public void setWebsite(String website) {
      this.website = website;
    }
  }

  @Configuration
  public static class SpringConfig {

    @Bean
    public API getAPIHandler() {
      final Config conf = ConfigFactory.load();
      return new API(
          PsqlContextHandler.getContext(
              "org.postgresql.Driver",
              conf.getString("db.host"),
              conf.getInt("db.port"),
              conf.getString("db.name"),
              conf.getString("db.username"),
              conf.getString("db.password")
          )
      );
    }
  }
}
