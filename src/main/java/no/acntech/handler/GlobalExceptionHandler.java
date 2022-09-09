package no.acntech.handler;

import io.micrometer.core.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Optional;

import no.acntech.model.Error;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class
    })
    @NonNull
    public ResponseEntity<Object> handleBadRequestException(@NonNull final Exception exception,
                                                            @NonNull final WebRequest request) {
        return handleException(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({
            IllegalStateException.class
    })
    @NonNull
    public ResponseEntity<Object> handleConflictException(@NonNull final Exception exception,
                                                          @NonNull final WebRequest request) {
        return handleException(exception, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({
            AuthenticationException.class
    })
    @NonNull
    public ResponseEntity<Object> handleUnauthorizedException(@NonNull final Exception exception,
                                                              @NonNull final WebRequest request) {
        return handleException(exception, HttpStatus.UNAUTHORIZED, request);
    }


    @ExceptionHandler({
            Exception.class
    })
    @NonNull
    public ResponseEntity<Object> handleServerErrorException(@NonNull final Exception exception,
                                                             @NonNull final WebRequest request) {
        return handleException(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(@NonNull final HttpRequestMethodNotSupportedException exception,
                                                                         @NonNull final HttpHeaders headers,
                                                                         @NonNull final HttpStatus status,
                                                                         @NonNull final WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(@NonNull final HttpMediaTypeNotSupportedException exception,
                                                                     @NonNull final HttpHeaders headers,
                                                                     @NonNull final HttpStatus status,
                                                                     @NonNull final WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(@NonNull final HttpMediaTypeNotAcceptableException exception,
                                                                      @NonNull final HttpHeaders headers,
                                                                      @NonNull final HttpStatus status,
                                                                      @NonNull final WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(@NonNull final MissingPathVariableException exception,
                                                               @NonNull final HttpHeaders headers,
                                                               @NonNull final HttpStatus status,
                                                               @NonNull final WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull final HttpMessageNotReadableException exception,
                                                                  @NonNull final HttpHeaders headers,
                                                                  @NonNull final HttpStatus status,
                                                                  @NonNull final WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(@NonNull final HttpMessageNotWritableException exception,
                                                                  @NonNull final HttpHeaders headers,
                                                                  @NonNull final HttpStatus status,
                                                                  @NonNull final WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull final MethodArgumentNotValidException exception,
                                                                  @NonNull final HttpHeaders headers,
                                                                  @NonNull final HttpStatus status,
                                                                  @NonNull final WebRequest request) {
        return handleException(exception, headers, status, request);
    }

    private ResponseEntity<Object> handleException(final Exception exception,
                                                   final HttpStatus httpStatus,
                                                   final WebRequest request) {
        return handleException(exception, new HttpHeaders(), httpStatus, request);
    }

    private ResponseEntity<Object> handleException(final Exception exception,
                                                   final HttpHeaders headers,
                                                   final HttpStatus httpStatus,
                                                   final WebRequest request) {
        final var resolvedHttpStatus = resolveHttpStatus(exception).orElse(httpStatus);
        final var messages = Collections.singletonList(exception.getLocalizedMessage());
        final var error = new Error(messages, false);
        LOGGER.error(exception.getLocalizedMessage(), exception);
        return handleExceptionInternal(exception, error, headers, resolvedHttpStatus, request);
    }

    private Optional<HttpStatus> resolveHttpStatus(final Exception exception) {
        final var annotation = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);
        return Optional.ofNullable(annotation)
                .map(ResponseStatus::code);
    }
}
