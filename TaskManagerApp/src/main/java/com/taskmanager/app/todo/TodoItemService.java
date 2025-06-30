package com.taskmanager.app.todo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.taskmanager.app.comment.Comment;
import com.taskmanager.app.comment.CommentCreationRequest;
import com.taskmanager.app.comment.CommentRepository;
import com.taskmanager.app.comment.CommentResponse;
import com.taskmanager.app.comment.CommentService;
import com.taskmanager.app.email.EmailService;
import com.taskmanager.app.project.Project;
import com.taskmanager.app.project.ProjectNotFoundException;
import com.taskmanager.app.project.ProjectRepository;
import com.taskmanager.app.project.ProjectResponse;
import com.taskmanager.app.project.ProjectService;
import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserNotFoundException;
import com.taskmanager.app.user.TaskUserRepository;
import com.taskmanager.app.user.TaskUserService;
import com.taskmanager.app.user.UserCreationRequest;
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

  public TodoItemService(TodoItemRepository todoItemRepository, TaskUserRepository userRepository,
      ProjectRepository projectRepository, ProjectService projectService, @Lazy TaskUserService userService,
      @Lazy CommentService commentService, CommentRepository commentRepository) {
    super();
    this.todoItemRepository = todoItemRepository;
    this.userRepository = userRepository;
    this.projectRepository = projectRepository;
    this.projectService = projectService;
    this.userService = userService;
    this.commentService = commentService;
    this.commentRepository = commentRepository;
  }

  public TodoItemResponse get(Long todoItemId) {
    TodoItem todoItem = todoItemRepository.findById(todoItemId).orElseThrow(
        () -> new TodoItemNotFoundException("Todo item not found with id " + todoItemId));
    return convertTodoItemToResponse(todoItem);
  }

  public TodoItemResponse getByUid(UUID todoUid) {
    TodoItem todoItem = todoItemRepository.findByTodoUid(todoUid).orElseThrow(
        () -> new TodoItemNotFoundException("Todo item not found with Uid " + todoUid));
    return convertTodoItemToResponse(todoItem);
  }


  public TodoItemResponse create(TodoItemCreationRequest todoItemCreationRequest) {
    TodoItem todoItem = convertRequestToTodoItem(todoItemCreationRequest);
    System.out.println("Saving TodoItem: " + todoItem); // Log before saving
    TodoItem todoItemSaved = todoItemRepository.save(todoItem);

    System.out.println("Saved entity description: " + todoItem.getDescription());

    return convertTodoItemToResponse(todoItemSaved);
  }

  public List<TodoItemResponse> getAllTodoItems() {
    return todoItemRepository.findAll().stream().map(this::convertTodoItemToResponse)
        .collect(Collectors.toList());
  }

  public TodoItemResponse update(UUID todoUid, TodoItemResponse todoItemResponse) {
	    TodoItem todoItem = todoItemRepository.findByTodoUid(todoUid)
	        .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with Uid " + todoUid));

	    // Update basic fields
	    todoItem.setTaskName(todoItemResponse.getTaskName());
	    todoItem.setDescription(todoItemResponse.getDescription());
	    todoItem.setComplete(todoItemResponse.getComplete());
	    todoItem.setDueBy(todoItemResponse.getDueBy());
	    todoItem.setStartDate(todoItemResponse.getStartDate());
	    todoItem.setPriority(todoItemResponse.getPriority());
	    todoItem.setStatus(todoItemResponse.getStatus());

	    // Handle user
	    if (todoItemResponse.getUser() != null && todoItemResponse.getUser().getUserUid() != null) {
	        TaskUser user = userRepository.findByUserUid(todoItemResponse.getUser().getUserUid())
	            .orElseThrow(() -> new TaskUserNotFoundException("User not found with Uid " + todoItemResponse.getUser().getUserUid()));
	        todoItem.setUser(user);
	    }

	    // Handle project
	    if (todoItemResponse.getProject() != null && todoItemResponse.getProject().getProjectUid() != null) {
	        Project project = projectRepository
	            .findByProjectUid(todoItemResponse.getProject().getProjectUid())
	            .orElseThrow(() -> new ProjectNotFoundException("Project not found with UID: " + todoItemResponse.getProject().getProjectUid()));
	        todoItem.setProject(project);
	    }

	    // Set status based on conditions
	    LocalDateTime now = LocalDateTime.now();
	    if (Boolean.TRUE.equals(todoItem.getComplete())) {
	        todoItem.setStatus(TodoStatus.COMPLETE);
	    } else if (todoItem.getStartDate().isAfter(now)) {
	        todoItem.setStatus(TodoStatus.PENDING);
	    } else {
	        todoItem.setStatus(TodoStatus.INPROGRESS);
	    }

	    TodoItem updatedItem = todoItemRepository.save(todoItem);
	    return convertTodoItemToResponse(updatedItem);
	}

  public void delete(UUID todoUid) {
    TodoItem todoItem = todoItemRepository.findByTodoUid(todoUid).orElseThrow(
        () -> new TodoItemNotFoundException("Todo item not found with Uid " + todoUid));

    todoItemRepository.delete(todoItem);
  }

  public List<TodoItemResponse> getAllTasksByUserUid(UUID userUid) {
    List<TodoItem> todoItems = todoItemRepository.findAllByUser_UserUid(userUid);
    return todoItems.stream().map(this::convertTodoItemToResponse).collect(Collectors.toList());
  }

  public List<TodoItemResponse> getAllTasksForProject(UUID projectUid) {
    List<TodoItem> todoItems = todoItemRepository.findAllByProject_ProjectUid(projectUid); //
    return todoItems.stream().map(this::convertTodoItemToResponse).collect(Collectors.toList());
  }

  

  public TodoItemResponse convertTodoItemToResponse(TodoItem todoItem) {
    TodoItemResponse todoItemResponse = new TodoItemResponse();

    todoItemResponse.setTodoUid(todoItem.getTodoUid());
    todoItemResponse.setTaskName(todoItem.getTaskName());
    todoItemResponse.setDescription(todoItem.getDescription());
    todoItemResponse.setComplete(todoItem.getComplete());
    todoItemResponse.setStartDate(todoItem.getStartDate());
    todoItemResponse.setDueBy(todoItem.getDueBy());
    todoItemResponse.setStatus(todoItem.getStatus());
    todoItemResponse.setPriority(todoItem.getPriority());

    // Map User to DTO
    if (todoItem.getUser() != null) {
      UserCreationResponse creationResponse = userService.convertEntityToResponse(todoItem.getUser());
      todoItemResponse.setUser(creationResponse);
    }

    // Map Project to DTO
    if (todoItem.getProject() != null) {
      ProjectResponse projectResponse =
          projectService.convertProjectEntityToResponse(todoItem.getProject());
      todoItemResponse.setProject(projectResponse);
    }

    // ✅ Map Comments to CommentResponses and set totalComments
    if (todoItem.getComments() != null && !todoItem.getComments().isEmpty()) {
      List<CommentResponse> commentResponses = todoItem.getComments().stream()
          .map(comment -> commentService.convertEntityToResponse(comment)).toList();
      todoItemResponse.setComments(commentResponses);
      todoItemResponse.setTotalComments(commentResponses.size()); // ✅ Set totalComments
    } else {
      todoItemResponse.setTotalComments(0); // ✅ Handle case when there are no comments
    }

    return todoItemResponse;
  }


  public TodoItem convertRequestToTodoItem(TodoItemCreationRequest todoItemCreationRequest) {
    TodoItem todoItem = new TodoItem();
    todoItem.setDescription(todoItemCreationRequest.getDescription());
    todoItem.setTodoUid(UUID.randomUUID());
    todoItem.setDueBy(todoItemCreationRequest.getDueBy());
    todoItem.setTaskName(todoItemCreationRequest.getTaskName());
    todoItem.setPriority(todoItemCreationRequest.getPriority());
    todoItem.setStartDate(todoItemCreationRequest.getStartDate());

    // Mapping TaskUser to TodoItem
    TaskUser user = userRepository.findByUserUid(todoItemCreationRequest.getUser())
        .orElseThrow(() -> new TaskUserNotFoundException(
            "User not found with Uid " + todoItemCreationRequest.getUser()));

    todoItem.setUser(user);

    // Mapping Project to TodoItem
    if (todoItemCreationRequest.getProject() != null) {
        Project project = projectRepository.findByProjectUid(todoItemCreationRequest.getProject())
            .orElseThrow(() -> 
                new ProjectNotFoundException("Project not found with uid " + todoItemCreationRequest.getProject())
            );
        todoItem.setProject(project);
    }

    return todoItem;
  }

  public static TaskUser convertResponseToEntity(UserCreationResponse response) {
      if (response == null) return null;

      TaskUser user = new TaskUser();
      user.setId(response.getId());
      user.setUserUid(response.getUserUid());
      user.setEmail(response.getEmail());
      user.setUsername(response.getName());
      user.setPassword(response.getPassword());

      if (response.getTodoItems() != null) {
          List<TodoItem> todoItems = new ArrayList<>();
          for (TodoItemResponse todoResponse : response.getTodoItems()) {
              TodoItem todo = new TodoItem();
              todo.setTodoUid(todoResponse.getTodoUid());
              todo.setTaskName(todoResponse.getTaskName());
              todo.setDescription(todoResponse.getDescription());
              todo.setComplete(todoResponse.getComplete());
              todo.setStatus(todoResponse.getStatus());
              todo.setStartDate(todoResponse.getStartDate());
              todo.setDueBy(todoResponse.getDueBy());
              todo.setPriority(todoResponse.getPriority());
              todo.setUser(user); // establish bidirectional link
              todoItems.add(todo);
          }
          user.setTodoItems(todoItems);
      }

      return user;
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
