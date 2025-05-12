package com.taskmanager.app.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.taskmanager.app.email.EmailService;
import com.taskmanager.app.project.Project;
import com.taskmanager.app.project.ProjectNotFoundException;
import com.taskmanager.app.project.ProjectRepository;
import com.taskmanager.app.project.ProjectResponse;
import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserNotFoundException;
import com.taskmanager.app.user.TaskUserRepository;
import com.taskmanager.app.user.UserCreationResponse;

@Service
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;
    private final TaskUserRepository taskUserRepository;
    private final ProjectRepository projectRepository;
    private final EmailService emailService;
    
    public TodoItemService(TodoItemRepository todoItemRepository, TaskUserRepository taskUserRepository, ProjectRepository projectRepository, EmailService emailService) {
        this.todoItemRepository = todoItemRepository;
		this.taskUserRepository = taskUserRepository;
		this.projectRepository = projectRepository;
		this.emailService = emailService;
    }

    public TodoItemResponse get(Long todoItemId) {
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with id " + todoItemId));
        return convertTodoItemToResponse(todoItem);
    }

    public TodoItemResponse create(TodoItemCreationRequest todoItemCreationRequest) {
        TodoItem todoItem = convertRequestToTodoItem(todoItemCreationRequest);
        System.out.println("Saving TodoItem: " + todoItem); // Log before saving
        TodoItem todoItemSaved = todoItemRepository.save(todoItem);

        // Send email if an assignee is present
        if (todoItemSaved.getTaskUser() != null && todoItemSaved.getTaskUser().getEmail() != null) {
            String toEmail = todoItemSaved.getTaskUser().getEmail();
            String taskName = todoItemSaved.getTaskName();

            emailService.sendTaskAssignmentEmail(toEmail, taskName);
        }

        return convertTodoItemToResponse(todoItemSaved);
    }

    public List<TodoItemResponse> getAllTodoItems() {
        return todoItemRepository.findAll().stream()
                .map(this::convertTodoItemToResponse)
                .collect(Collectors.toList());
    }

    public TodoItemResponse update(Long todoItemId, TodoItemResponse todoItemResponse) {
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with id " + todoItemId));
        
        todoItem.setDescription(todoItemResponse.getDescription());
        todoItem.setComplete(todoItemResponse.getComplete());
        todoItem.setDueBy(todoItemResponse.getDueBy());
        todoItem.setStatus(todoItemResponse.getStatus());
        
        TaskUser taskUser = taskUserRepository.findById(todoItemResponse.getUser().getId())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with id " + todoItemResponse.getUser()));

        todoItem.setTaskUser(taskUser);

        
        if(todoItem.getComplete()==true) {
        	todoItem.setStatus(TodoStatus.COMPLETE);
        }

        TodoItem todoItemUpdated = todoItemRepository.save(todoItem);
        return convertTodoItemToResponse(todoItemUpdated);
    }

    public void delete(Long todoItemId) {
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with id " + todoItemId));
        
        todoItemRepository.delete(todoItem);
    }
    
    public List<TodoItemResponse> getAllTasksByUserId(Long userId) {
        List<TodoItem> todoItems = todoItemRepository.getAllTasksByTaskUserId(userId);
        return todoItems.stream()
                .map(this::convertTodoItemToResponse)
                .collect(Collectors.toList());
    }
    
    public List<TodoItemResponse> getAllTasksForProject(Long projectId) {
        List<TodoItem> todoItems = todoItemRepository.findByProjectId(projectId); // Fetch tasks by project
        return todoItems.stream()
                .map(this::convertTodoItemToResponse)
                .collect(Collectors.toList());
    }



    public TodoItemResponse convertTodoItemToResponse(TodoItem todoItem) {
        TodoItemResponse todoItemResponse = new TodoItemResponse();

        todoItemResponse.setId(todoItem.getId());
        todoItemResponse.setTaskName(todoItem.getTaskName());
        todoItemResponse.setDescription(todoItem.getDescription());
        todoItemResponse.setComplete(todoItem.getComplete());
        todoItemResponse.setDueBy(todoItem.getDueBy());
        todoItemResponse.setStatus(todoItem.getStatus());

        // Map TaskUser to UserCreationResponse
        if (todoItem.getTaskUser() != null) {
            UserCreationResponse userResponse = new UserCreationResponse();
            userResponse.setId(todoItem.getTaskUser().getId());
            userResponse.setUsername(todoItem.getTaskUser().getUsername());
            userResponse.setEmail(todoItem.getTaskUser().getEmail());
            todoItemResponse.setUser(userResponse);
        }

        // Map Project to ProjectResponse
        if (todoItem.getProject() != null) {
            ProjectResponse projectResponse = new ProjectResponse();
            projectResponse.setId(todoItem.getProject().getId());
            projectResponse.setName(todoItem.getProject().getName());
            projectResponse.setDescription(todoItem.getProject().getDescription());

            // Ensure the todoItems list is not null before streaming
            List<TodoItemResponse> todoItemResponses = (todoItem.getProject().getTodoItems() != null ?
                    todoItem.getProject().getTodoItems() : new ArrayList<>()) // Use an empty list if null
                    .stream()
                    .map(todo -> {
                        TodoItemResponse todoItemResp = new TodoItemResponse();
                        if (todo instanceof TodoItem) {  // Ensure it's of type TodoItem
                            TodoItem t = (TodoItem) todo;  // Cast to TodoItem explicitly
                            todoItemResp.setId(t.getId());
                            todoItemResp.setTaskName(t.getTaskName());
                            todoItemResp.setDescription(t.getDescription());
                            todoItemResp.setComplete(t.getComplete());
                            todoItemResp.setDueBy(t.getDueBy());
                            todoItemResp.setStatus(t.getStatus());
                        }
                        return todoItemResp;
                    })
                    .collect(Collectors.toList());

            projectResponse.setTodoItems(todoItemResponses);
            todoItemResponse.setProject(projectResponse);
        }

        return todoItemResponse;
    }


    public TodoItem convertRequestToTodoItem(TodoItemCreationRequest todoItemCreationRequest) {
        TodoItem todoItem = new TodoItem();
        todoItem.setDescription(todoItemCreationRequest.getDescription());
        todoItem.setDueBy(todoItemCreationRequest.getDueBy());
        todoItem.setTaskName(todoItemCreationRequest.getTaskName());

        // Mapping TaskUser to TodoItem
        TaskUser taskUser = taskUserRepository.findById(todoItemCreationRequest.getUser())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with id " + todoItemCreationRequest.getUser()));

        todoItem.setTaskUser(taskUser);

        // Mapping Project to TodoItem
        if (todoItemCreationRequest.getProject() != null) {
            Project project = projectRepository.findById(todoItemCreationRequest.getProject())
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with id " + todoItemCreationRequest.getProject()));
            todoItem.setProject(project);
        }

        return todoItem;
    }
    
    public TodoItem convertDtoToTodoItem(TodoItemResponse todoItemResponse) {
        TodoItem todoItem = new TodoItem();
        
        todoItem.setId(todoItemResponse.getId());
        todoItem.setDescription(todoItemResponse.getDescription());
        todoItem.setDueBy(todoItemResponse.getDueBy());
        todoItem.setComplete(todoItemResponse.getComplete());
        todoItem.setStatus(todoItemResponse.getStatus());
        todoItem.setTaskName(todoItemResponse.getTaskName());
        
        TaskUser taskUser = taskUserRepository.findById(todoItemResponse.getUser().getId())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with id " + todoItemResponse.getUser()));

        todoItem.setTaskUser(taskUser);
        
        return todoItem;
    }
    
    public TodoItemCreationRequest convertResponseToTodoItemRequest(TodoItemResponse todoItemResponse) {
        TodoItemCreationRequest todoItemCreationRequest = new TodoItemCreationRequest();

        // Mapping fields from TodoItemResponse to TodoItemCreationRequest
        todoItemCreationRequest.setDescription(todoItemResponse.getDescription());
        todoItemCreationRequest.setDueBy(todoItemResponse.getDueBy());

        // Mapping TaskUser (if needed)
        if (todoItemResponse.getUser() != null) {
            todoItemCreationRequest.setUser(todoItemResponse.getUser().getId());
        }

        // Assuming project is optional, if it exists in the response, map it
        if (todoItemResponse.getProject() != null) {
            todoItemCreationRequest.setProject(todoItemResponse.getProject().getId());
        }

        return todoItemCreationRequest;
    }


   
}
