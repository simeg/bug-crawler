package app.ui;

import app.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class ViewController {

  private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);

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
    if (api.isRunning()) {
      LOG.warn("URL is already being worked on - will not start working on [{}]", options.getUrl());
      // TODO: Warn user via an JS alert or something
      return "index";
    }

    api.runApp(options.getUrl());
    return "running";
  }

  @ResponseBody
  @RequestMapping(value = "/bugs", method = RequestMethod.GET)
  public Response getBugs(Model model) {
    return new Response(api.getAllBugs().size());
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

  private static class Response {

    private int noOfBugs;

    public Response(int noOfBugs) {
      this.noOfBugs = noOfBugs;
    }

    public int getNoOfBugs() {
      return noOfBugs;
    }

    public void setNoOfBugs(int noOfBugs) {
      this.noOfBugs = noOfBugs;
    }
  }

}
