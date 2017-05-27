package app.analyze;

import com.google.common.base.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Bug {

  private static final Logger LOG = LoggerFactory.getLogger(Bug.class);

  public enum BugType {
    UNKNOWN, FILE_ACCESS, XSS, HTML
  }

  public final BugType type;
  public final String baseUrl;
  public final String description;
  public final Optional<String> path;

  Bug(BugType type, String baseUrl, String description, Optional<String> path) {
    this.type = type;
    this.baseUrl = baseUrl;
    this.description = description;
    this.path = path;
  }

  public static Bug create(BugType type, String baseUrl, String description, Optional<String> path) {
    if (!Enums.getIfPresent(BugType.class, type.name()).isPresent()) {
      LOG.warn("{}: Bug type not found: {}", Thread.currentThread().getName(), type);
      type = BugType.UNKNOWN;
    }

    return new Bug(type, baseUrl, description, path);
  }

  @Override
  public String toString() {
    // TODO: about `this.path.get()`, make sure it has a value before using it
    return String.format("[%s]: %s", this.type.toString(), this.path.get());
  }

}
