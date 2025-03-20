package com.taskmanager.app.user;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.taskmanager.app.project.Project;
import com.taskmanager.app.project.ProjectResponse;
import com.taskmanager.app.role.Role;
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
				.orElseThrow(() -> new TaskUserNotFoundException("User not found with id " + userId));
		return convertEntityToResponse(taskUser);

	}

	public UserCreationResponse create(UserCreationRequest userCreationRequest) {
		TaskUser taskUser = convertRequestToEntity(userCreationRequest);
		System.out.println("Saving User: " + taskUser); // Log before saving
		TaskUser taskUserSaved = taskUserRepository.save(taskUser);
		return convertEntityToResponse(taskUserSaved);
	}

	public List<UserCreationResponse> getAll() {
		return taskUserRepository.findAll().stream().map(this::convertEntityToResponse).collect(Collectors.toList());
	}

	@Transactional
	public UserCreationResponse update(Long userId, UserCreationResponse userCreationResponse) {
		System.out.println("🔍 Checking user existence for ID: " + userId);

		TaskUser taskUser = taskUserRepository.findById(userId).orElseThrow(() -> {
			System.out.println("❌ User NOT FOUND with ID: " + userId);
			return new TodoItemNotFoundException("User not found with id " + userId);
		});

		System.out.println("✅ User found: " + taskUser.getUsername());

		taskUser.setUsername(userCreationResponse.getUsername());
		taskUser.setEmail(userCreationResponse.getEmail());

		List<TodoItem> updatedTodoItems = userCreationResponse.getTodoItems().stream()
				.map(todoItemService::convertDtoToTodoItem).collect(Collectors.toList());

		taskUser.setTodoItems(updatedTodoItems);
		Set<Role> roles = userCreationResponse.getRoles().stream().map(roleId -> {
			Role role = new Role();
			role.setId(roleId);
			return role;
		}).collect(Collectors.toSet());

		taskUser.setRoles(roles);

		taskUser = taskUserRepository.save(taskUser);

		System.out.println("✔ User updated successfully.");

		return convertEntityToResponse(taskUser);
	}

	public void delete(Long userId) {
		TaskUser taskUser = taskUserRepository.findById(userId)
				.orElseThrow(() -> new TaskUserNotFoundException("User not found with id " + userId));

		taskUserRepository.delete(taskUser);
	}

	public List<UserCreationResponse> getUsersByRole(Long roleId) {
		List<TaskUser> users = taskUserRepository.findByRolesId(roleId);
		return users.stream().map(this::convertEntityToResponse).collect(Collectors.toList());
	}
	
	public UserCreationResponse findByEmail(String email) {
	    TaskUser user = taskUserRepository.findByEmail(email)
	            .orElseThrow(() -> new TaskUserNotFoundException("User not found with email " + email));
	    return convertEntityToResponse(user);
	}
	
    public boolean emailExists(final String email) {
        return taskUserRepository.existsByEmailIgnoreCase(email);
    }



	// Mapper functions below
	
    private UserCreationResponse convertEntityToResponse(TaskUser taskUser) {
        UserCreationResponse userCreationResponse = new UserCreationResponse();

        userCreationResponse.setUsername(taskUser.getUsername());
        userCreationResponse.setEmail(taskUser.getEmail());
        userCreationResponse.setId(taskUser.getId());

        // Convert TodoItems to TodoItemResponses
        userCreationResponse.setTodoItems(taskUser.getTodoItems().stream().map(todoItem -> {
            TodoItemResponse todoItemResponse = todoItemService.convertTodoItemToResponse(todoItem);

            // If the TodoItem has a Project, convert the Project entity to ProjectResponse
            if (todoItem.getProject() != null) {
                ProjectResponse projectResponse = new ProjectResponse();
                projectResponse.setId(todoItem.getProject().getId());
                projectResponse.setName(todoItem.getProject().getName());
                projectResponse.setDescription(todoItem.getProject().getDescription());

                // Convert TodoItems associated with this Project into TodoItemResponses and add to projectResponse
                List<TodoItemResponse> projectTodoItems = todoItem.getProject().getTodoItems().stream()
                        .map(todoItemService::convertTodoItemToResponse)
                        .collect(Collectors.toList());

                projectResponse.setTodoItems(projectTodoItems);

                todoItemResponse.setProject(projectResponse);
            }

            return todoItemResponse;
        }).collect(Collectors.toList()));

        // Convert Set<Role> to List of Role IDs
        List<Long> roleIds = taskUser.getRoles().stream().map(Role::getId) // Get role ID
                .collect(Collectors.toList());

        userCreationResponse.setRoles(roleIds); // Set the roles as a list of role IDs

        return userCreationResponse;
    }


	private TaskUser convertResponseToEntity(UserCreationResponse userCreationResponse) {
	    TaskUser taskUser = new TaskUser();

	    taskUser.setUsername(userCreationResponse.getUsername());
	    taskUser.setEmail(userCreationResponse.getEmail());
	    taskUser.setId(userCreationResponse.getId());

	    // Convert TodoItemResponses to TodoItems
	    taskUser.setTodoItems(userCreationResponse.getTodoItems().stream()
	            .map(todoItemResponse -> {
	                TodoItem todoItem = new TodoItem();
	                todoItem.setDescription(todoItemResponse.getDescription());
	                todoItem.setDueBy(todoItemResponse.getDueBy());
	                todoItem.setComplete(todoItemResponse.getComplete());
	                todoItem.setStatus(todoItemResponse.getStatus());

	                // If TaskUser is required in TodoItem
	                todoItem.setTaskUser(taskUser);

	                // Convert ProjectResponse to Project if present
	                if (todoItemResponse.getProject() != null) {
	                    Project project = new Project();
	                    project.setId(todoItemResponse.getProject().getId());
	                    project.setName(todoItemResponse.getProject().getName());
	                    project.setDescription(todoItemResponse.getProject().getDescription());
	                    // Assuming other necessary fields can be set here
	                    todoItem.setProject(project);
	                }

	                return todoItem;
	            })
	            .collect(Collectors.toList()));

	    // Convert role IDs to Role entities
	    Set<Role> roles = userCreationResponse.getRoles().stream().map(roleId -> {
	        Role role = new Role();
	        role.setId(roleId); // Assuming you have Role IDs to look up from DB
	        return role;
	    }).collect(Collectors.toSet());

	    taskUser.setRoles(roles);

	    return taskUser;
	}

	
	private TaskUser convertRequestToEntity(UserCreationRequest userCreationRequest) {
		TaskUser taskUser = new TaskUser();

		taskUser.setUsername(userCreationRequest.getUsername());
		taskUser.setEmail(userCreationRequest.getEmail());
		taskUser.setPassword(userCreationRequest.getPassword());

		// Convert List of Role IDs to Set<Role>
		Set<Role> roles = userCreationRequest.getRoles().stream().map(roleId -> {
			Role role = new Role();
			role.setId(roleId);
			return role;
		}).collect(Collectors.toSet());

		taskUser.setRoles(roles);

		return taskUser;
	}

	private TodoItemCreationRequest convertTodoResponseToTodoRequest(TodoItemResponse response) {
		TodoItemCreationRequest request = new TodoItemCreationRequest();
		request.setDescription(response.getDescription());
		request.setDueBy(response.getDueBy());
		return request;
	}

}
