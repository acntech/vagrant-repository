package no.acntech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class CannotSaveItemException extends RuntimeException {

    public CannotSaveItemException(String message) {
        super(message);
    }

    public CannotSaveItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
