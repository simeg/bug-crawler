package app.url;

public class UrlParseException extends Exception {

  public UrlParseException() {
  }

  public UrlParseException(String message) {
    super(message);
  }

  public UrlParseException(String message, Throwable cause) {
    super(message, cause);
  }
}

