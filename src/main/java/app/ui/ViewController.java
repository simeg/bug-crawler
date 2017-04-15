package app.ui;

import app.analyze.Bug;
import app.api.API;
import app.api.CrawlerAPI;
import app.queue.QueueSupervisor;
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
    model.addAttribute("subLinkQueueSize", QueueSupervisor.getSubLinksInQueue());
    model.addAttribute("urlQueueSize", QueueSupervisor.getCrawledLinksInQueue());
    model.addAttribute("bugQueueSize", QueueSupervisor.getBugsLinksInQueue());

    // All bugs are here. Now map to some unordered list
    List<Bug> bugs = this.api.getAllBugs();

    return "index";
  }
}
