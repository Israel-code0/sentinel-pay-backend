package org.israel.sentinelpay.exception;

import org.israel.sentinelpay.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FintechException.class)
    public ResponseEntity<ApiResponse<String>> handleFintechException(FintechException ex) {
        ApiResponse<String> response = new ApiResponse<>(
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
