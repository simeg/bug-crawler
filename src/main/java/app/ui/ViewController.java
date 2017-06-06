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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
public class ViewController {

  private final API api;

  public ViewController() {
    final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
    api = context.getBean(API.class);
  }

  @RequestMapping("/")
  public String indexAction(ModelMap model) {
//     model.addAttribute("subLinkQueueSize", supervisor.get(QueueId.TO_BE_CRAWLED).size());
    // model.addAttribute("urlQueueSize", supervisor.get(QueueId.TO_BE_ANALYZED).size());
    // model.addAttribute("bugQueueSize", supervisor.get(QueueId.TO_BE_STORED_AS_BUG).size());

    return "index";
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
