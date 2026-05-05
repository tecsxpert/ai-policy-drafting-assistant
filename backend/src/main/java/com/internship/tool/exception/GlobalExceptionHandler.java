package com.internship.tool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Handles exceptions globally
public class GlobalExceptionHandler {

    // Handle ResourceNotFoundException (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {

      ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            404,
            "Not Found",
            ex.getMessage()
    );

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}

    // Handle Validation Exception (400)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
     public ResponseEntity<ErrorResponse> handleValidationException(
        org.springframework.web.bind.MethodArgumentNotValidException ex) {

        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->
            validationErrors.put(err.getField(), err.getDefaultMessage())
    );

    ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Bad Request",
            validationErrors
    );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
   }

    // Handle all other exceptions (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

    ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            500,
            "Internal Server Error",
            ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {

       ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            400,
            "Bad Request",
            ex.getMessage()
    );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}