package com.fdcapi.exception;

public class ApiException extends RuntimeException {

    private final ErrorCodes errorCode;
    private final int httpStatus;

    public ApiException(ErrorCodes errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
    }

    public ApiException(ErrorCodes errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
    }

    public ErrorCodes getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
