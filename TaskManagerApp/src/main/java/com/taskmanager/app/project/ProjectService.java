package com.taskmanager.app.project;

import com.taskmanager.app.todo.TodoItem;
import com.taskmanager.app.todo.TodoItemResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // Create a new project
    public ProjectResponse createProject(ProjectCreationRequest projectCreationRequest) {
        Project project = convertRequestToEntity(projectCreationRequest);
        Project savedProject = projectRepository.save(project);
        return convertProjectEntityToResponse(savedProject);
    }

    // Get a project by ID
    public ProjectResponse getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + projectId));
        return convertProjectEntityToResponse(project);
    }

    // Get all projects
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertProjectEntityToResponse)
                .collect(Collectors.toList());
    }

    // **Update a project using ProjectResponse**
    public ProjectResponse updateProject(Long projectId, ProjectResponse updatedResponse) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with ID: " + projectId));

        // Update fields
        project.setName(updatedResponse.getName());
        project.setDescription(updatedResponse.getDescription());

        // Convert TodoItemResponses to TodoItems and update
        if (updatedResponse.getTodoItems() != null) {
            List<TodoItem> todoItems = updatedResponse.getTodoItems().stream()
                    .map(this::convertTodoResponseToEntity)
                    .collect(Collectors.toList());
            project.setTodoItems(todoItems);
        }

        Project updatedProject = projectRepository.save(project);
        return convertProjectEntityToResponse(updatedProject);
    }

    // Delete a project
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found with ID: " + projectId);
        }
        projectRepository.deleteById(projectId);
    }

    // Convert ProjectCreationRequest to Project entity
    private Project convertRequestToEntity(ProjectCreationRequest projectCreationRequest) {
        Project project = new Project();
        project.setName(projectCreationRequest.getName());
        project.setDescription(projectCreationRequest.getDescription());
        return project;
    }

    // Convert Project entity to ProjectResponse
    private ProjectResponse convertProjectEntityToResponse(Project project) {
        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setId(project.getId());
        projectResponse.setName(project.getName());
        projectResponse.setDescription(project.getDescription());

        if (project.getTodoItems() != null) {
            List<TodoItemResponse> todoItemResponses = project.getTodoItems().stream()
                    .map(this::convertTodoEntityToResponse)
                    .collect(Collectors.toList());
            projectResponse.setTodoItems(todoItemResponses);
        }

        return projectResponse;
    }

    // Convert TodoItemResponse to TodoItem entity
    private TodoItem convertTodoResponseToEntity(TodoItemResponse todoResponse) {
        TodoItem todoItem = new TodoItem();
        todoItem.setId(todoResponse.getId());
        todoItem.setDescription(todoResponse.getDescription());
        todoItem.setDueBy(todoResponse.getDueBy());
        todoItem.setComplete(todoResponse.getComplete());
        todoItem.setStatus(todoResponse.getStatus());
        return todoItem;
    }

    // Convert TodoItem entity to TodoItemResponse
    private TodoItemResponse convertTodoEntityToResponse(TodoItem todoItem) {
        TodoItemResponse todoResponse = new TodoItemResponse();
        todoResponse.setId(todoItem.getId());
        todoResponse.setDescription(todoItem.getDescription());
        todoResponse.setDueBy(todoItem.getDueBy());
        todoResponse.setComplete(todoItem.getComplete());
        todoResponse.setStatus(todoItem.getStatus());
        return todoResponse;
    }
}
