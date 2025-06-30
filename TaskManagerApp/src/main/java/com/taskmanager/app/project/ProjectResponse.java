package com.taskmanager.app.project;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.taskmanager.app.todo.TodoItem;
import com.taskmanager.app.todo.TodoItemResponse;

import io.swagger.v3.oas.annotations.media.Schema;
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
	
    @Schema(example = "7614b501-169a-476b-a0ce-0beb1e7f8693")
    private UUID projectUid;
    
    private String name;

    private String description;
    
    private List<TodoItemResponse> todoItems;

    
}
