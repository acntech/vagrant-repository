package no.acntech.handler;

import io.micrometer.core.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.UrlPathHelper;

import javax.validation.ConstraintViolationException;

import no.acntech.exception.UnknownProviderException;
import no.acntech.model.Error;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            IllegalArgumentException.class,
            UnknownProviderException.class,
            ConstraintViolationException.class
    })
    @NonNull
    public ResponseEntity<Object> handleBadRequestException(@NonNull Exception exception,
                                                            @NonNull WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({
            IllegalStateException.class
    })
    @NonNull
    public ResponseEntity<Object> handleConflictException(@NonNull Exception exception,
                                                          @NonNull WebRequest request) {
        return handleException(exception, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({
            AuthenticationException.class
    })
    @NonNull
    public ResponseEntity<Object> handleUnauthorizedException(@NonNull Exception exception,
                                                              @NonNull WebRequest request) {
        return handleException(exception, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({
            Exception.class
    })
    @NonNull
    public ResponseEntity<Object> handleServerErrorException(@NonNull Exception exception,
                                                             @NonNull WebRequest request) {
        return handleException(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(@NonNull HttpRequestMethodNotSupportedException exception,
                                                                         @NonNull HttpHeaders headers,
                                                                         @NonNull HttpStatus status,
                                                                         @NonNull WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(@NonNull HttpMediaTypeNotSupportedException exception,
                                                                     @NonNull HttpHeaders headers,
                                                                     @NonNull HttpStatus status,
                                                                     @NonNull WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(@NonNull HttpMediaTypeNotAcceptableException exception,
                                                                      @NonNull HttpHeaders headers,
                                                                      @NonNull HttpStatus status,
                                                                      @NonNull WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(@NonNull MissingPathVariableException exception,
                                                               @NonNull HttpHeaders headers,
                                                               @NonNull HttpStatus status,
                                                               @NonNull WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(@NonNull HttpMessageNotWritableException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    private ResponseEntity<Object> handleException(Exception exception, HttpStatus httpStatus, WebRequest request) {
        return handleException(exception, new HttpHeaders(), httpStatus, request);
    }

    private ResponseEntity<Object> handleException(Exception exception, HttpHeaders headers, HttpStatus httpStatus, WebRequest request) {
        Error error = Error.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(exception.getLocalizedMessage())
                .path(findRequestPath(request))
                .build();
        LOGGER.error(exception.getLocalizedMessage(), exception);
        return handleExceptionInternal(exception, error, headers, httpStatus, request);
    }

    private String findRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return new UrlPathHelper().getPathWithinApplication(servletWebRequest.getRequest());
        } else {
            return request.getContextPath();
        }
    }
}
