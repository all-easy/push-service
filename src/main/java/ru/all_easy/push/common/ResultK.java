package ru.all_easy.push.common;

public class ResultK<T, E> {
    private T result;
    private E error;

    private ResultK(T result, E error) {
        this.result = result;
        this.error = error;
    }

    public static <T, E> ResultK<T, E> Err(E error) {
        return new ResultK(null, error);
    }

    public static <T, E> ResultK<T, E> Ok(T result) {
        return new ResultK(result, null);
    }

    public boolean hasError() {
        return error != null;
    }

    public T getResult() {
        return result;
    }

    public E getError() {
        return error;
    }
}
