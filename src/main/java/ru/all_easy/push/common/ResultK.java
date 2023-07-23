package ru.all_easy.push.common;

public class ResultK {
    private final Result result;
    private final Error error;

    private ResultK(Result result, Error error) {
        this.result = result;
        this.error = error;
    }

    public static ResultK Err(Error error) {
        return new ResultK(null, error);
    }

    public static ResultK Ok(Result result) {
        return new ResultK(result, null);
    }

    public boolean hasError() {
        return error != null;
    }

    public Result getResult() {
        return result;
    }

    public Error getError() {
        return error;
    }
}
