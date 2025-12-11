package com.fdcapi.exception;

public enum ErrorCodes {
    
    // Client errors (4xx)
    INVALID_REQUEST(400, "Invalid request parameters"),
    FOOD_NOT_FOUND(404, "Food item not found"),
    INVALID_API_KEY(401, "Invalid or missing API key"),
    
    // Server errors (5xx)
    EXTERNAL_API_ERROR(502, "Error communicating with Food Data Central API"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    SERVICE_UNAVAILABLE(503, "Service temporarily unavailable");

    private final int httpStatus;
    private final String message;

    ErrorCodes(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
