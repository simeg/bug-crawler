package app.analyze;

import java.util.Optional;

public class Bug {
  private final String url;
  private final String reason;
  private final Optional<String> urlPath;

  Bug(String url, String reason, Optional<String> accessPath) {
    this.url = url;
    this.reason = reason;
    this.urlPath = accessPath;
  }
}
