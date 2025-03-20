package com.taskmanager.app.todo;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.taskmanager.app.config.CustomOffsetDateTimeDeserializer;
import com.taskmanager.app.project.ProjectResponse;
import com.taskmanager.app.user.UserCreationResponse;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoItemResponse {
	
    private Long id;
		
	@NotNull
	private String description;
	
	@NotNull
	private LocalDateTime dueBy;
	
	@NotNull
	private Boolean complete;
	
    @NotNull
    private TodoStatus status; // Add status field
    
    private UserCreationResponse user;
    
    private ProjectResponse project;

	
}
