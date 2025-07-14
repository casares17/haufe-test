package com.haufe.test.exception;

public class ServerException extends Exception {

    public ServerException(Throwable ex, String message) {
        super(message, ex);
    }

    public ServerException(String message) {
        super(message);
    }
}
