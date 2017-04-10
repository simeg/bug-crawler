package app.analyze;

import com.google.common.base.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Bug {

  private static final Logger LOG = LoggerFactory.getLogger(Bug.class);

  private final BugType type;
  private final String url;
  private final String description;
  private final Optional<String> path;

  Bug(BugType type, String url, String description, Optional<String> path) {
    this.type = type;
    this.url = url;
    this.description = description;
    this.path = path;
  }

  public static Bug create(BugType type, String url, String description, Optional<String> path) {
    if (!Enums.getIfPresent(BugType.class, type.name()).isPresent()) {
      LOG.error("{}: Bug type not found: {}", Thread.currentThread().getName(), type);
      type = BugType.UNKNOWN;
    }

    return new Bug(type, url, description, path);
  }

  @Override
  public String toString() {
    return String.format("[%s]: %s", this.type.toString(), this.url);
  }

  public enum BugType {
    UNKNOWN, FILE_ACCESS, XSS
  }
}
