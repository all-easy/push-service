package ru.all_easy.push.shape.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ru.all_easy.push.common.Result;
import ru.all_easy.push.shape.service.ShapeServiceException;

@ControllerAdvice
public class ShapeServiceExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ShapeServiceException.class)
    public ResponseEntity<Result<String>> handleAuthException(ShapeServiceException exc) {
        return ResponseEntity
                .status(exc.getCode())
                .body(new Result<>(exc.getMessage(), exc.getCode()));
    }
}
