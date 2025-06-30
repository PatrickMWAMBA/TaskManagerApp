package com.taskmanager.app.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.taskmanager.app.todo.TodoItemResponse;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreationResponse {
	
    private Long id;

    @NotNull
    @UserUserUidUnique
    private UUID userUid;

    @NotNull
    @Size(max = 255)
    @UserEmailUnique
    private String email;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    @Size(max = 255)
    private String password;

    private List<Long> roles;

    private List<String> roleNames;
    
    List<TodoItemResponse> todoItems = new ArrayList<>();

  
}
