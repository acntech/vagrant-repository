package no.acntech.exception;

public class SaveItemFailedException extends RuntimeException {

    public SaveItemFailedException(String message) {
        super(message);
    }

    public SaveItemFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
