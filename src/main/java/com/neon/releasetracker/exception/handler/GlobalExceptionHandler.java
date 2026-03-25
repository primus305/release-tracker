package com.neon.releasetracker.exception.handler;

import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ReleaseNotFoundException.class)
    public ResponseEntity<Object> handleReleaseNotFoundException(ReleaseNotFoundException ex, Locale locale) {
        log.error(ex.getMessageKey(), ex);

        String message = messageSource.getMessage(ex.getMessageKey(), ex.getArgs(), locale);

        log.error(message);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(NOT_FOUND.value())
                .message(message)
                .errorCode(NOT_FOUND.name())
                .build();

        return ResponseEntity.status(NOT_FOUND).body(errorResponse);
    }
}
