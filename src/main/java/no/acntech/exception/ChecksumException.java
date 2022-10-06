package no.acntech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class ChecksumException extends RuntimeException {

    public ChecksumException(String message) {
        super(message);
    }

    public ChecksumException(String message, Throwable cause) {
        super(message, cause);
    }
}
