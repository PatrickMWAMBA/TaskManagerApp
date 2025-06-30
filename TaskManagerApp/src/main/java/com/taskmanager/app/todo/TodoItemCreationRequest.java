package com.taskmanager.app.todo;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TodoItemCreationRequest {

  @NotNull
  private String taskName;

  private String description;

  private LocalDateTime startDate;

  private LocalDateTime dueBy;

  private UUID user;

  private UUID project;

  @NotNull
  private PriorityLevel priority;

}
