package ru.all_easy.push.web.security;

public class JwtException extends RuntimeException {

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
