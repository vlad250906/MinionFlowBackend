package ru.vlad2509.minionflow.application.exception;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.vlad2509.minionflow.MyApplication;

public class ApiException extends RuntimeException {

    private final int httpStatusCode;
    private final String errorCode;

    public ApiException(int httpStatusCode, String errorCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
    }

    public ApiException(ApiError apiError) {
        super(apiError.getMessage());
        this.httpStatusCode = apiError.getHttpStatusCode();
        this.errorCode = apiError.getErrorCode();
    }

    public ApiException(ApiError apiError, String debugMessage) {
        super(MyApplication.IS_DEV ? debugMessage : apiError.getMessage());
        this.httpStatusCode = apiError.getHttpStatusCode();
        this.errorCode = apiError.getErrorCode();
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
