package com.taskmanager.app.todo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.taskmanager.app.comment.Comment;
import com.taskmanager.app.comment.CommentRepository;
import com.taskmanager.app.comment.CommentResponse;
import com.taskmanager.app.comment.CommentService;
import com.taskmanager.app.project.Project;
import com.taskmanager.app.project.ProjectNotFoundException;
import com.taskmanager.app.project.ProjectRepository;
import com.taskmanager.app.project.ProjectResponse;
import com.taskmanager.app.project.ProjectService;
import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserNotFoundException;
import com.taskmanager.app.user.TaskUserRepository;
import com.taskmanager.app.user.TaskUserService;
import com.taskmanager.app.user.UserCreationResponse;

@Service
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;
    private final TaskUserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final TaskUserService userService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    public TodoItemService(
            TodoItemRepository todoItemRepository,
            TaskUserRepository userRepository,
            ProjectRepository projectRepository,
            ProjectService projectService,
            @Lazy TaskUserService userService,
            @Lazy CommentService commentService,
            CommentRepository commentRepository) {

        this.todoItemRepository = todoItemRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    public TodoItemResponse get(Long todoItemId) {
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with id " + todoItemId));
        return convertTodoItemToResponse(todoItem);
    }

    public TodoItemResponse getByUid(UUID todoUid) {
        TodoItem todoItem = todoItemRepository.findWithCommentsByTodoUid(todoUid)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with Uid " + todoUid));
        return convertTodoItemToResponse(todoItem);
    }

    public TodoItemResponse create(TodoItemCreationRequest request) {
        TodoItem todoItem = convertRequestToTodoItem(request);
        TodoItem saved = todoItemRepository.save(todoItem);
        return convertTodoItemToResponse(saved);
    }

    public List<TodoItemResponse> getAllTodoItems() {
        return todoItemRepository.findAll().stream()
                .map(this::convertTodoItemToResponse)
                .collect(Collectors.toList());
    }

    public TodoItemResponse update(UUID todoUid, TodoItemResponse response) {
        TodoItem todoItem = todoItemRepository.findByTodoUid(todoUid)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with Uid " + todoUid));

        todoItem.setTaskName(response.getTaskName());
        todoItem.setDescription(response.getDescription());
        todoItem.setComplete(response.getComplete());
        todoItem.setDueBy(response.getDueBy());
        todoItem.setStartDate(response.getStartDate());
        todoItem.setPriority(response.getPriority());
        todoItem.setStatus(response.getStatus());

        if (response.getUser() != null && response.getUser().getUserUid() != null) {
            TaskUser user = userRepository.findByUserUid(response.getUser().getUserUid())
                    .orElseThrow(() -> new TaskUserNotFoundException("User not found with Uid " + response.getUser().getUserUid()));
            todoItem.setUser(user);
        }

        if (response.getProject() != null && response.getProject().getProjectUid() != null) {
            Project project = projectRepository.findByProjectUid(response.getProject().getProjectUid())
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with UID: " + response.getProject().getProjectUid()));
            todoItem.setProject(project);
        }

        LocalDateTime now = LocalDateTime.now();
        if (Boolean.TRUE.equals(todoItem.getComplete())) {
            todoItem.setStatus(TodoStatus.COMPLETE);
        } else if (todoItem.getStartDate().isAfter(now)) {
            todoItem.setStatus(TodoStatus.PENDING);
        } else {
            todoItem.setStatus(TodoStatus.INPROGRESS);
        }

        TodoItem updated = todoItemRepository.save(todoItem);
        return convertTodoItemToResponse(updated);
    }

    public void delete(UUID todoUid) {
        TodoItem todoItem = todoItemRepository.findByTodoUid(todoUid)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with Uid " + todoUid));
        todoItemRepository.delete(todoItem);
    }

    public List<TodoItemResponse> getAllTasksByUserUid(UUID userUid) {
        List<TodoItem> todoItems = todoItemRepository.findAllByUser_UserUid(userUid);
        return todoItems.stream()
                .map(this::convertTodoItemToResponse)
                .collect(Collectors.toList());
    }

    public List<TodoItemResponse> getAllTasksForProject(UUID projectUid) {
        List<TodoItem> todoItems = todoItemRepository.findAllByProject_ProjectUid(projectUid);
        return todoItems.stream()
                .map(this::convertTodoItemToResponse)
                .collect(Collectors.toList());
    }

    public TodoItemResponse convertTodoItemToResponse(TodoItem todoItem) {
        TodoItemResponse response = new TodoItemResponse();

        response.setTodoUid(todoItem.getTodoUid());
        response.setTaskName(todoItem.getTaskName());
        response.setDescription(todoItem.getDescription());
        response.setComplete(todoItem.getComplete());
        response.setStartDate(todoItem.getStartDate());
        response.setDueBy(todoItem.getDueBy());
        response.setStatus(todoItem.getStatus());
        response.setPriority(todoItem.getPriority());

        // Map only basic fields to avoid recursion
        if (todoItem.getUser() != null) {
            TaskUser user = todoItem.getUser();
            UserCreationResponse userDto = new UserCreationResponse();
            userDto.setUserUid(user.getUserUid());
            userDto.setName(user.getUsername());
            userDto.setEmail(user.getEmail());

            // Important: prevent recursion by not setting todoItems or roles
            userDto.setTodoItems(null);
            userDto.setRoles(null);

            response.setUser(userDto);
        }

        if (todoItem.getProject() != null) {
            Project loadedProject = projectRepository.findByProjectUid(todoItem.getProject().getProjectUid())
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with UID: " + todoItem.getProject().getProjectUid()));
            ProjectResponse projectDto = projectService.convertProjectEntityToResponse(loadedProject);
            response.setProject(projectDto);
        }

        if (todoItem.getComments() != null && !todoItem.getComments().isEmpty()) {
            List<CommentResponse> commentResponses = todoItem.getComments().stream()
                    .map(commentService::convertEntityToResponse)
                    .collect(Collectors.toList());
            response.setComments(commentResponses);
            response.setTotalComments(commentResponses.size());
        } else {
            response.setTotalComments(0);
        }

        return response;
    }

    public TodoItem convertRequestToTodoItem(TodoItemCreationRequest request) {
        TodoItem todoItem = new TodoItem();
        todoItem.setTodoUid(UUID.randomUUID());
        todoItem.setTaskName(request.getTaskName());
        todoItem.setDescription(request.getDescription());
        todoItem.setStartDate(request.getStartDate());
        todoItem.setDueBy(request.getDueBy());
        todoItem.setPriority(request.getPriority());

        TaskUser user = userRepository.findByUserUid(request.getUser())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with Uid " + request.getUser()));
        todoItem.setUser(user);

        if (request.getProject() != null) {
            Project project = projectRepository.findByProjectUid(request.getProject())
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with uid " + request.getProject()));
            todoItem.setProject(project);
        }

        return todoItem;
    }
    
    public TodoItem convertResponseToEntity(TodoItemResponse todoItemResponse) {
	    TodoItem todoItem = new TodoItem();

	    todoItem.setTodoUid(todoItemResponse.getTodoUid());
	    todoItem.setTaskName(todoItemResponse.getTaskName());
	    todoItem.setDescription(todoItemResponse.getDescription());
	    todoItem.setComplete(todoItemResponse.getComplete());
	    todoItem.setDueBy(todoItemResponse.getDueBy());
	    todoItem.setStartDate(todoItemResponse.getStartDate());
	    todoItem.setStatus(todoItemResponse.getStatus());
	    todoItem.setPriority(todoItemResponse.getPriority());

	    // Convert user (only set if UID is present)
	    if (todoItemResponse.getUser() != null && todoItemResponse.getUser().getUserUid() != null) {
	        TaskUser user = userRepository.findByUserUid(todoItemResponse.getUser().getUserUid())
	            .orElseThrow(() -> new TaskUserNotFoundException("User not found with UID: " + todoItemResponse.getUser().getUserUid()));
	        todoItem.setUser(user);
	    }

	    // Convert project (only set if UID is present)
	    if (todoItemResponse.getProject() != null && todoItemResponse.getProject().getProjectUid() != null) {
	        Project project = projectRepository.findByProjectUid(todoItemResponse.getProject().getProjectUid())
	            .orElseThrow(() -> new ProjectNotFoundException("Project not found with UID: " + todoItemResponse.getProject().getProjectUid()));
	        todoItem.setProject(project);
	    }

	    return todoItem;
	}
}
