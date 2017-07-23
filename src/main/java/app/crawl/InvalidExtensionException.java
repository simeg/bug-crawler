package app.crawl;

public class InvalidExtensionException extends Exception {

  public InvalidExtensionException() {
  }

  public InvalidExtensionException(String message) {
    super(message);
  }

  public InvalidExtensionException(String message, Throwable cause) {
    super(message, cause);
  }
}
