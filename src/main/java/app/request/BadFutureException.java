package app.request;

public class BadFutureException extends Exception {

  public BadFutureException() {
  }

  public BadFutureException(String message) {
    super(message);
  }

  public BadFutureException(String message, Throwable cause) {
    super(message, cause);
  }
}

