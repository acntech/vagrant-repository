package no.acntech.exception;

import java.io.Serial;

public class UnknownProviderException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3030673371829244178L;

    public UnknownProviderException(String message) {
        super(message);
    }

    public UnknownProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
