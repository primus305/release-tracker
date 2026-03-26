package com.neon.releasetracker.exception.handler;

import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.response.ErrorResponse;
import com.neon.releasetracker.response.ValidationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    public static final String ERROR_TYPE_MISMATCH_KEY = "error.type.mismatch";
    public static final String ERROR_VALIDATION_FAILED = "error.validation.failed";
    public static final String ERROR_GENERAL = "error.general";

    private final MessageSource messageSource;

    @ExceptionHandler(ReleaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReleaseNotFoundException(ReleaseNotFoundException ex,
                                                                        Locale locale) {
        log.warn("Release not found", ex);

        String message = resolveMessage(ex.getMessageKey(), ex.getArgs(), locale);

        return buildErrorResponse(NOT_FOUND, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                               Locale locale) {
        log.warn("Validation error occurred", ex);

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ValidationError.builder()
                        .field(error.getField())
                        .message(messageSource.getMessage(error, locale))
                        .build())
                .toList();

        String message = resolveMessage(ERROR_VALIDATION_FAILED, new Object[]{}, locale);

        return buildValidationErrorResponse(message, errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
                                                                                Locale locale) {
        log.warn("Validation error occurred", ex);

        List<ValidationError> errors = ex.getParameterValidationResults().stream()
                .flatMap(result -> mapToValidationErrors(locale, result))
                .toList();

        String message = resolveMessage(ERROR_VALIDATION_FAILED, new Object[]{}, locale);

        return buildValidationErrorResponse(message, errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex, Locale locale) {
        log.warn("Validation error occurred", ex);

        String type = Optional.ofNullable(ex.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("unknown");

        Object value = Optional.ofNullable(ex.getValue()).orElse("unknown");

        String message = messageSource.getMessage(
                ERROR_TYPE_MISMATCH_KEY,
                new Object[]{ex.getName(), value, type},
                locale
        );

        return buildErrorResponse(BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception ex, Locale locale) {
        log.error("Unexpected error occurred", ex);

        String message = messageSource.getMessage(ERROR_GENERAL, new Object[]{}, locale);

        return buildErrorResponse(INTERNAL_SERVER_ERROR, message);
    }

    private String resolveMessage(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .errorCode(status.name())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<ErrorResponse> buildValidationErrorResponse(String message,
                                                                       List<ValidationError> errors) {

        ErrorResponse response = ErrorResponse.builder()
                .status(BAD_REQUEST.value())
                .errorCode(BAD_REQUEST.name())
                .message(message)
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    private Stream<ValidationError> mapToValidationErrors(Locale locale, ParameterValidationResult result) {
        if (result instanceof ParameterErrors parameterErrors) {
            return mapParameterErrors(locale, parameterErrors);
        }

        return mapResolvableErrors(locale, result);
    }

    private Stream<ValidationError> mapResolvableErrors(Locale locale, ParameterValidationResult result) {
        String fieldName = resolveParameterName(result);

        return result.getResolvableErrors()
                .stream()
                .map(error -> ValidationError.builder()
                        .field(fieldName)
                        .message(messageSource.getMessage(error, locale))
                        .build());
    }

    private Stream<ValidationError> mapParameterErrors(Locale locale, ParameterErrors parameterErrors) {
        return parameterErrors.getFieldErrors()
                .stream()
                .map(error -> ValidationError.builder()
                        .field(error.getField())
                        .message(messageSource.getMessage(error, locale))
                        .build());
    }

    private String resolveParameterName(ParameterValidationResult result) {
        MethodParameter param = result.getMethodParameter();

        RequestParam requestParam = param.getParameterAnnotation(RequestParam.class);
        if (requestParam != null && !requestParam.value().isBlank()) {
            return requestParam.value();
        }

        PathVariable pathVariable = param.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null && !pathVariable.value().isBlank()) {
            return pathVariable.value();
        }

        return Optional.ofNullable(param.getParameterName())
                .orElse(param.getParameter().getName());
    }

}
