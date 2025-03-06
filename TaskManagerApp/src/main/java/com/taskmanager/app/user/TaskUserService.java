package com.taskmanager.app.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.taskmanager.app.todo.TodoItem;
import com.taskmanager.app.todo.TodoItemCreationRequest;
import com.taskmanager.app.todo.TodoItemNotFoundException;
import com.taskmanager.app.todo.TodoItemResponse;
import com.taskmanager.app.todo.TodoItemService;
import com.taskmanager.app.todo.TodoStatus;

import jakarta.transaction.Transactional;

@Service
public class TaskUserService {

	private TaskUserRepository taskUserRepository;

	private final TodoItemService todoItemService;

	public TaskUserService(TaskUserRepository taskUserRepository, TodoItemService todoItemService) {
		super();
		this.todoItemService = todoItemService;
		this.taskUserRepository = taskUserRepository;
	}

	public UserCreationResponse get(Long userId) {

		TaskUser taskUser = taskUserRepository.findById(userId)
				.orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with id " + userId));
		return convertTaskUserToDto(taskUser);

	}
	
    public UserCreationResponse create(UserCreationRequest userCreationRequest) {
        TaskUser taskUser = convertDtoToTaskUser(userCreationRequest);
        System.out.println("Saving User: " + taskUser); // Log before saving
        TaskUser taskUserSaved = taskUserRepository.save(taskUser);
        return convertTaskUserToDto(taskUserSaved);
    }

    public List<UserCreationResponse> getAll() {
        return taskUserRepository.findAll().stream()
                .map(this::convertTaskUserToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserCreationResponse update(Long userId, UserCreationResponse userCreationResponse) {
        System.out.println("🔍 Checking user existence for ID: " + userId);
        
        TaskUser taskUser = taskUserRepository.findById(userId)
                .orElseThrow(() -> {
                    System.out.println("❌ User NOT FOUND with ID: " + userId);
                    return new TodoItemNotFoundException("User not found with id " + userId);
                });

        System.out.println("✅ User found: " + taskUser.getUsername());

        taskUser.setUsername(userCreationResponse.getUsername());
        taskUser.setEmail(userCreationResponse.getEmail());

        List<TodoItem> updatedTodoItems = userCreationResponse.getTodoItems().stream()
                .map(todoItemService::convertDtoToTodoItem)
                .collect(Collectors.toList());

        taskUser.setTodoItems(updatedTodoItems);

        taskUser = taskUserRepository.save(taskUser);

        System.out.println("✔ User updated successfully.");

        return convertTaskUserToDto(taskUser);
    }
    
    public Long delete(Long userId) {
    	TaskUser taskUser = taskUserRepository.findById(userId)
                .orElseThrow(() -> new TodoItemNotFoundException("User not found with id " + userId));
        
    	taskUserRepository.delete(taskUser);
        return userId;
    }


    
    //Mapper functions below 
    
	private UserCreationResponse convertTaskUserToDto(TaskUser taskUser) {
		UserCreationResponse userCreationResponse = new UserCreationResponse();

		userCreationResponse.setUsername(taskUser.getUsername());
		userCreationResponse.setEmail(taskUser.getEmail());

		// Convert TodoItems to TodoItemResponses
		userCreationResponse
				.setTodoItems(taskUser.getTodoItems().stream().map(todoItemService::convertTodoItemToDto) 
					.collect(Collectors.toList()));

		return userCreationResponse;
	}

	private TaskUser convertDtoToTaskUser(UserCreationResponse userCreationResponse) {
		TaskUser taskUser = new TaskUser();

		taskUser.setUsername(userCreationResponse.getUsername());
		taskUser.setEmail(userCreationResponse.getEmail());

		// Convert TodoItemResponses to TodoItems
		taskUser.setTodoItems(userCreationResponse.getTodoItems().stream()
				.map(response -> todoItemService.convertDtoToTodoItem(convertResponseToRequest(response)))
				.collect(Collectors.toList()));

		return taskUser;
	}
	
	private TaskUser convertDtoToTaskUser(UserCreationRequest userCreationRequest) {
		TaskUser taskUser = new TaskUser();

		taskUser.setUsername(userCreationRequest.getUsername());
		taskUser.setEmail(userCreationRequest.getEmail());
		taskUser.setPassword(userCreationRequest.getPassword());

		return taskUser;
	}


	private TodoItemCreationRequest convertResponseToRequest(TodoItemResponse response) {
		TodoItemCreationRequest request = new TodoItemCreationRequest();
		request.setDescription(response.getDescription());
		request.setDueBy(response.getDueBy());
		return request;
	}

}
