package com.taskmanager.app.comment;

import java.util.UUID;

import com.taskmanager.app.todo.TodoItemResponse;
import com.taskmanager.app.user.UserCreationResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {

	private Long id;

	private UUID commentUid;

	private String content;

	private TodoItemResponse todoItem;

	private UserCreationResponse author;

}