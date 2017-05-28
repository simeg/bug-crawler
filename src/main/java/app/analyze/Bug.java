package app.analyze;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Bug {

  private static final Logger LOG = LoggerFactory.getLogger(Bug.class);

  public enum BugType {
    FILE_ACCESS, XSS, HTML
  }

  public final BugType type;
  public final String baseUrl;
  public final String description;
  public final Optional<String> path;

  public Bug(BugType type, String baseUrl, String description, Optional<String> path) {
    this.type = type;
    this.baseUrl = baseUrl;
    this.description = description;
    this.path = path;
  }

  @Override
  public String toString() {
    String path = "";
    if (this.path.isPresent()) {
      path = this.path.get();
    }

    return String.format("[%s]: %s", this.type.toString(), path);
  }

}
