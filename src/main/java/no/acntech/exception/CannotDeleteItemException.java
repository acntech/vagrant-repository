package no.acntech.exception;

public class CannotDeleteItemException extends RuntimeException {

    public CannotDeleteItemException(String message) {
        super(message);
    }

    public CannotDeleteItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
