package com.taskmanager.app.todo;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.taskmanager.app.config.CustomOffsetDateTimeDeserializer;
import com.taskmanager.app.config.CustomOffsetDateTimeSerializer;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoItemCreationRequest {
	
	@NotNull
	private String taskName;
			
	@NotNull
	private String description;
	
	@NotNull
	private LocalDateTime dueBy;
	
    private Long user;
    
    private Long project;

	
	
}
