package app.ui;

import app.api.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@EnableAutoConfiguration
public class ViewController {

  private final API api;

  @Autowired
  public ViewController(API api) {
    this.api = api;
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String indexForm(Model model) {
    model.addAttribute("options", new Options());
    return "index";
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public String formSubmit(@ModelAttribute Options options) {
    api.runApp(options.getUrl());
    return "running";
  }

  private static class Options {

    private String url;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }
}
