package com.fdcapi.exception;

public class FdcApiException extends RuntimeException {
    public FdcApiException(String message) {
        super(message);
    }

    public FdcApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
