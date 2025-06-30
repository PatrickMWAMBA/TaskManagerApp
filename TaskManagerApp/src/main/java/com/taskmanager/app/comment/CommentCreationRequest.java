package com.taskmanager.app.comment;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreationRequest {

  @NotBlank(message = "Comment content must not be blank")
  private String content;

  @NotNull(message = "Todo Uid must not be null")
  private UUID todoUid;

  @NotNull(message = "Author Uid must not be null")
  private UUID authorUid;
  
}
