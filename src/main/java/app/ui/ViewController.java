package app.ui;

import app.queue.QueueSupervisor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@EnableAutoConfiguration
public class ViewController {

  @RequestMapping("/{name}")
  public String indexAction(ModelMap model, @PathVariable("name") String name) {
    model.addAttribute("subLinkQueueSize", QueueSupervisor.subLinkQueue.size());
    model.addAttribute("urlQueueSize", QueueSupervisor.crawledLinkQueue.size());
    model.addAttribute("bugQueueSize", QueueSupervisor.bugsQueue.size());

    return "index";
  }
}
