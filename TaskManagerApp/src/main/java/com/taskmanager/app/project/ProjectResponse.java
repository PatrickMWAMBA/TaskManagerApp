package com.taskmanager.app.project;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.taskmanager.app.todo.TodoItem;
import com.taskmanager.app.todo.TodoItemResponse;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectResponse {
	
    private Long id;
    
    private String name;

    private String description;
    
    private List<TodoItemResponse> todoItems;

    
}
