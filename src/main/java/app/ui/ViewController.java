package app.ui;

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
    model.addAttribute("name", name);

    return "index";
  }
}
