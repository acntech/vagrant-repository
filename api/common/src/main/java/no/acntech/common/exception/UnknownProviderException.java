package no.acntech.common.exception;

public class UnknownProviderException extends RuntimeException {

    private static final long serialVersionUID = -3030673371829244178L;

    public UnknownProviderException(String message) {
        super(message);
    }

    public UnknownProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
