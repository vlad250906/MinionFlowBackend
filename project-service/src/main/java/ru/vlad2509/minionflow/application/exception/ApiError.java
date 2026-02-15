package ru.vlad2509.minionflow.application.exception;

public enum ApiError {

    I_AM_A_TEAPOT(418, "iAmTeaPot", "I am a teapot (WIP)"),
    UNEXPECTED_ERROR(500, "unexpectedError", "Unexpected error");

    private final int httpStatusCode;
    private final String errorCode;
    private final String defaultMessage;

    ApiError(int httpStatusCode, String errorCode, String defaultMessage) {
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.defaultMessage = defaultMessage;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return defaultMessage;
    }
}
