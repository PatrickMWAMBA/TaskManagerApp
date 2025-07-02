package com.taskmanager.app.exceptions;

import org.springframework.http.HttpStatus;
import com.taskmanager.app.exceptions.ErrorResponse;  // Your custom ErrorResponse class

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.taskmanager.app.todo.TodoItemNotFoundException;
import com.taskmanager.app.project.ProjectNotFoundException;
import com.taskmanager.app.user.TaskUserNotFoundException;
import com.taskmanager.app.comment.CommentNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TodoItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTodoItemNotFoundException(TodoItemNotFoundException ex) {
        // Create an ErrorResponse object with the message from the exception
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

        // Return the error response wrapped in ResponseEntity with NOT_FOUND status
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(ProjectNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TaskUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskUserNotFoundException(TaskUserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}



