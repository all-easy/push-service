package ru.all_easy.push.common;

public record Result<T>(T payload, String error, ErrorType type, Integer errorCode) {

    public Result(T payload) {
        this(payload, null, null, null);
    }

    public Result(String message, int code) {
        this(message, null, code);
    }

    public Result(String message, ErrorType errorType, int code) {
        this(null, message, errorType, code);
    }
}
