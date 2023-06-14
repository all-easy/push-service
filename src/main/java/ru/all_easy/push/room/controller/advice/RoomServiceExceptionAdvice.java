package ru.all_easy.push.room.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.all_easy.push.common.Result;
import ru.all_easy.push.room.service.exception.RoomServiceException;

@ControllerAdvice
public class RoomServiceExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RoomServiceException.class)
    public ResponseEntity<Result<String>> handleAuthException(RoomServiceException exc) {
        return ResponseEntity.status(exc.getCode())
                .body(new Result<>(exc.getMessage(), exc.getErrorType(), exc.getCode()));
    }
}
