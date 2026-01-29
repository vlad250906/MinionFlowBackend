package ru.vlad2509.minionflow.application.exception;

public enum ApiError {

    UNAUTHORIZED(401, "unauthorized", "Unauthorized"),
    ACCOUNT_SUSPENDED(403, "accountSuspended", "Account was suspended. Contact the support"),
    EMAIL_NOT_VERIFIED(403, "emailNotVerified", "Account's email is not verified"),
    INVALID_CREDENTIALS(401, "invalidCredentials", "Invalid email/username or password"),
    LOGIN_NOT_ENOUGH(422, "loginNotEnough", "provide at least one of: email, username"),
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
