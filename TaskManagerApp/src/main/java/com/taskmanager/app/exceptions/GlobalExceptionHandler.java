package com.taskmanager.app.exceptions;

import org.springframework.http.HttpStatus;
import com.taskmanager.app.exceptions.ErrorResponse;  // Your custom ErrorResponse class

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.taskmanager.app.todo.TodoItemNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TodoItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTodoItemNotFoundException(TodoItemNotFoundException ex) {
        // Create an ErrorResponse object with the message from the exception
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        
        // Return the error response wrapped in ResponseEntity with NOT_FOUND status
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}



