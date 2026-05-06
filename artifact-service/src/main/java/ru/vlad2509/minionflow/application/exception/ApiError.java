package ru.vlad2509.minionflow.application.exception;

public enum ApiError {

    TASK_CANCEL_FAIL(409, "taskCancelFailed", "Task could not be cancelled at this state"),
    MICROTASK_NOT_FOUND(404, "microtaskNotFound", "Microtask not found"),
    OUTPUT_NOT_READY(409, "outputNotAvailable", "Output is not available for this task"),
    INVALID_EXECUTION_CONFIG(400, "invalidExecutionConfig", "Invalid execution config"),
    TASK_NOT_FOUND(404, "taskNotFound", "Task run not found"),
    EXECUTION_CONFIG_NOT_FOUND(404, "executionConfigNotFound", "Execution config not found"),
    INPUT_NOT_FOUND(404, "inputNotFound", "Input artifact not found"),
    JAR_NOT_FOUND(404, "jarNotFound", "Jar artifact not found"),
    ARTIFACT_NOT_FOUND(404, "artifactNotFound", "Artifact not found"),
    PROJECT_NOT_FOUND(404, "projectNotFound", "Project not found"),
    INSUFFICIENT_PERMISSION(403, "insufficientPermission", "Insufficient permissions"),
    UNAUTHORIZED(401, "unauthorized", "Unauthorized"),
    I_AM_A_TEAPOT(418, "iAmTeaPot", "I am a teapot (WIP)"),
    S3_UNAVAILABLE(500, "s3_Unavailable", "S3 service is unavailable. Try again later"),
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
