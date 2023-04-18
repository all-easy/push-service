package ru.all_easy.push.common;

public class AbstractException extends RuntimeException {

    protected String message;
    protected ErrorType errorType;
    protected int code;

    @Override
    public String getMessage() {
        return message;
    }

    public AbstractException setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public AbstractException setCode(int code) {
        this.code = code;
        return this;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public AbstractException setErrorType(ErrorType errorType) {
        this.errorType = errorType;
        return this;
    }
}
