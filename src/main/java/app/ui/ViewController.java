package app.ui;

import app.analyze.Bug;
import app.persist.PsqlPersister;
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

  @RequestMapping("/")
  public String indexAction(ModelMap model) {
    model.addAttribute("subLinkQueueSize", QueueSupervisor.subLinkQueue.size());
    model.addAttribute("urlQueueSize", QueueSupervisor.crawledLinkQueue.size());
    model.addAttribute("bugQueueSize", QueueSupervisor.bugsQueue.size());

    final Config conf = ConfigFactory.load();
    final int port = conf.getInt("db.port");
    final String host = conf.getString("db.host");
    final String name = conf.getString("db.name");
    final String username = conf.getString("db.username");
    final String password = conf.getString("db.password");

    PsqlPersister psqlPersister =
        PsqlPersister.create("org.postgresql.Driver", host, port, name, username, password);
    // All bugs are here. Now map to some unordered list
    List<Bug> bugs = psqlPersister.getAllBugs();

    return "index";
  }
}
