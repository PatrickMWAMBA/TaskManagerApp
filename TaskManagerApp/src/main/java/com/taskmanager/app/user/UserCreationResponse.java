package com.taskmanager.app.user;

import java.util.ArrayList;
import java.util.List;

import com.taskmanager.app.todo.TodoItemResponse;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreationResponse {
	
	private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;
    
    List<TodoItemResponse> todoItems = new ArrayList<>();
    
    private List<Long> roles;

}
