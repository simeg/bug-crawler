package app.ui;

import app.analyze.Bug;
import app.api.API;
import app.api.CrawlerAPI;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@EnableAutoConfiguration
public class ViewController {

  private final API api;

  public ViewController() {
    final Config conf = ConfigFactory.load();
    final int port = conf.getInt("db.port");
    final String host = conf.getString("db.host");
    final String name = conf.getString("db.name");
    final String username = conf.getString("db.username");
    final String password = conf.getString("db.password");

    this.api = CrawlerAPI.create("org.postgresql.Driver", host, port, name, username, password);
  }

  @RequestMapping("/")
  public String indexAction(ModelMap model) {
    // TODO Inject QueueSupervisor
    // model.addAttribute("subLinkQueueSize", supervisor.get(QueueId.SUBLINK).size());
    // model.addAttribute("urlQueueSize", supervisor.get(QueueId.CRAWLED).size());
    // model.addAttribute("bugQueueSize", supervisor.get(QueueId.BUG).size());

    // All bugs are here. Now map to some unordered list
    List<Bug> bugs = this.api.getAllBugs();

    return "index";
  }
}
