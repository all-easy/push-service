package ru.all_easy.push.user.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ru.all_easy.push.common.Result;
import ru.all_easy.push.user.exception.UserServiceException;

@ControllerAdvice
public class UserServiceExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<Result<String>> handleAuthException(UserServiceException exc) {
        return ResponseEntity
                .status(exc.getErrorCode())
                .body(new Result<>(exc.getMessage(), exc.getErrorCode()));
    }

}
