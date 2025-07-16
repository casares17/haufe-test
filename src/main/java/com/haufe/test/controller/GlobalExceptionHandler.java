package com.haufe.test.controller;

import com.haufe.test.dto.ErrorResponse;
import com.haufe.test.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AccessDeniedException.class})
    ResponseEntity<ErrorResponse> accessDeniedExceptionHandler(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler({NotFoundException.class})
    ResponseEntity<ErrorResponse> notFoundExceptionHandler(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler({Exception.class})
    ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
