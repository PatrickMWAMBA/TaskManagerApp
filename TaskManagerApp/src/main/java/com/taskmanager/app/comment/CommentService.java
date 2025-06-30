package com.taskmanager.app.comment;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.taskmanager.app.todo.TodoItem;
import com.taskmanager.app.todo.TodoItemNotFoundException;
import com.taskmanager.app.todo.TodoItemRepository;
import com.taskmanager.app.todo.TodoItemService;
import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserNotFoundException;
import com.taskmanager.app.user.TaskUserRepository;
import com.taskmanager.app.user.TaskUserService;
import com.taskmanager.app.user.UserCreationResponse;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskUserRepository userRepository;
    private final TodoItemRepository todoItemRepository;
    private final TaskUserService userService;
    private final TodoItemService todoItemService;

    public CommentService(CommentRepository commentRepository, TaskUserRepository userRepository,
                          TodoItemRepository todoItemRepository, TaskUserService userService,
                          TodoItemService todoItemService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.todoItemRepository = todoItemRepository;
        this.userService = userService;
        this.todoItemService = todoItemService;
    }

    // CREATE
    public CommentResponse createComment(CommentCreationRequest request) {
        Comment comment = convertRequestToEntity(request);
        comment.setCommentUid(UUID.randomUUID());
        comment = commentRepository.save(comment);
        return convertEntityToResponse(comment);
    }

    // READ BY UID
    public CommentResponse getCommentByUid(UUID commentUid) {
        Comment comment = commentRepository.findByCommentUid(commentUid)
                .orElseThrow(()-> new CommentNotFoundException("Comment not found with UID "+commentUid));
        return convertEntityToResponse(comment);
    }

    // READ ALL
    public List<CommentResponse> getAllComments() {
        return commentRepository.findAll().stream()
                .map(this::convertEntityToResponse)
                .collect(Collectors.toList());
    }

    // READ ALL FOR TODO
    public List<CommentResponse> getAllCommentsForTodo(UUID todoUid) {
        return commentRepository.findAllByTodoItem_TodoUid(todoUid).stream()
                .map(this::convertEntityToResponse)
                .collect(Collectors.toList());
    }

    // READ ALL BY AUTHOR
    public List<CommentResponse> getAllCommentsByAuthor(UUID userUid) {
        return commentRepository.findByAuthor_UserUid(userUid).stream()
                .map(this::convertEntityToResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    public CommentResponse updateComment(UUID commentUid, CommentResponse commentResponse) {
        Comment existingComment = commentRepository.findByCommentUid(commentUid)
                .orElseThrow(()-> new CommentNotFoundException("Comment not found with UID "+commentUid));

        TodoItem todoItem = todoItemRepository.findByTodoUid(commentResponse.getTodoItem().getTodoUid())
                .orElseThrow(() -> new TodoItemNotFoundException("TodoItem not found with Uid: " + commentResponse.getTodoItem().getTodoUid()));

        TaskUser author = userRepository.findByUserUid(commentResponse.getAuthor().getUserUid())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with UID: " + commentResponse.getAuthor()));

        existingComment.setContent(commentResponse.getContent());
        existingComment.setAuthor(author);
        existingComment.setTodoItem(todoItem);

        Comment updatedComment = commentRepository.save(existingComment);
        return convertEntityToResponse(updatedComment);
    }

    // DELETE
    public void deleteComment(UUID commentUid) {
        Comment comment = commentRepository.findByCommentUid(commentUid)
                .orElseThrow(()-> new CommentNotFoundException("Comment not found with UID "+commentUid));
        commentRepository.delete(comment);
    }

    // Mapping: Request -> Entity
    public Comment convertRequestToEntity(CommentCreationRequest request) {
        Comment comment = new Comment();

        TaskUser author = userRepository.findByUserUid(request.getAuthorUid())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with UID: " + request.getAuthorUid()));

        TodoItem todoItem = todoItemRepository.findByTodoUid(request.getTodoUid())
            .orElseThrow(() -> new TodoItemNotFoundException("TodoItem not found with UID: " + request.getTodoUid()));

        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setTodoItem(todoItem);

        return comment;
    }

    // Mapping: Entity -> Response
    public CommentResponse convertEntityToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();

        UserCreationResponse userDTO = new UserCreationResponse();
        response.setId(comment.getId());
        response.setCommentUid(comment.getCommentUid());
        response.setAuthor(userService.convertEntityToResponse(comment.getAuthor()));
        response.setContent(comment.getContent());
        response.setTodoItem(todoItemService.convertTodoItemToResponse(comment.getTodoItem()));

        return response;
    }
}
