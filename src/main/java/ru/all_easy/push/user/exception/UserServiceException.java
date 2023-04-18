package ru.all_easy.push.user.exception;

public class UserServiceException extends RuntimeException {

    private String message;
    private int errorCode;

    public UserServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    public UserServiceException setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
