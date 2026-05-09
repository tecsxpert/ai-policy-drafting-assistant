package com.internship.tool.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;



@Data // Lombok: getters/setters
@AllArgsConstructor // constructor
public class ErrorResponse {

    private LocalDateTime timestamp; // time of error
    private int status;              // HTTP status code
    private String error;            // error type
    private Object message;          // message (String or Map for validation)
}