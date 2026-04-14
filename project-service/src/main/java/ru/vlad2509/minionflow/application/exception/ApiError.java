package ru.vlad2509.minionflow.application.exception;

public enum ApiError {

    USERNAME_NOT_FOUND(404, "usernameNotFound", "User with such username not found"),
    OWNER_LEAVE(409, "leaveImpossible", "Project owner can't leave the project"),
    OWNER_CONFLICT(409, "ownerConflict", "There can not be more than one OWNER in this project"),
    ALREADY_MEMBER(409, "alreadyMember", "User is already member of this project"),
    PROJECT_ALREADY_EXISTS(409, "projectAlreadyExists", "Project with this name already exists"),
    MEMBER_NOT_FOUND(404, "memberNotFound", "Member of the project not found"),
    PROJECT_NOT_FOUND(404, "projectNotFound", "Project not found"),
    INSUFFICIENT_PERMISSION(403, "insufficientPermission", "Insufficient permissions"),
    UNAUTHORIZED(401, "unauthorized", "Unauthorized"),
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
