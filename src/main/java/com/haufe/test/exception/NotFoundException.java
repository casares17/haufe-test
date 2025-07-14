package com.haufe.test.exception;

public class NotFoundException extends Exception {

    public NotFoundException(Throwable ex, String message) {
        super(message, ex);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
