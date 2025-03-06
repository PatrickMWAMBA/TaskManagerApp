package com.taskmanager.app.user;

public class TaskUserNotFoundException extends RuntimeException {
    public TaskUserNotFoundException(String message) {
        super(message);
    }
}
