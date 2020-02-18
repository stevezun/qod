package edu.cnm.deepdive.qod.controller.exception;

public class SearchTermTooShortException extends IllegalArgumentException {

  public SearchTermTooShortException() {
  }

  public SearchTermTooShortException(String s) {
    super( s );
  }

  public SearchTermTooShortException(String message, Throwable cause) {
    super( message, cause );
  }

  public SearchTermTooShortException(Throwable cause) {
    super( cause );
  }
}
