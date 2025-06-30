package com.taskmanager.app.todo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taskmanager.app.comment.CommentResponse;
import com.taskmanager.app.project.ProjectResponse;
import com.taskmanager.app.user.UserCreationResponse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoItemResponse {

  private UUID todoUid;

  private String taskName;

  private String description;

  private LocalDateTime dueBy;

  private LocalDateTime startDate;

  private Boolean complete;

  private TodoStatus status;

  private PriorityLevel priority;

  private UserCreationResponse user;

  private ProjectResponse project;

  private List<CommentResponse> comments;

  private int totalComments;
}
