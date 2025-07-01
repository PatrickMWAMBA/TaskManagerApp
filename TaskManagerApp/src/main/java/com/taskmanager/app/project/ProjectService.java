package com.taskmanager.app.project;

import com.taskmanager.app.todo.TodoItem;
import com.taskmanager.app.todo.TodoItemResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
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

    // Get a project by UUID
    public ProjectResponse getProjectByUid(UUID projectUid) {
        Project project = projectRepository.findByProjectUid(projectUid)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with UID: " + projectUid));
        return convertProjectEntityToResponse(project);
    }

    // Get all projects
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertProjectEntityToResponse)
                .collect(Collectors.toList());
    }

    // Update a project using its UID
    public ProjectResponse updateProject(UUID projectUid, ProjectResponse updatedResponse) {
        Project project = projectRepository.findByProjectUid(projectUid)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with UID: " + projectUid));

        project.setName(updatedResponse.getName());
        project.setDescription(updatedResponse.getDescription());

        if (updatedResponse.getTodoItems() != null) {
            List<TodoItem> todoItems = updatedResponse.getTodoItems().stream()
                    .map(this::convertTodoResponseToEntity)
                    .collect(Collectors.toList());
            project.setTodoItems(todoItems);
        }

        Project updatedProject = projectRepository.save(project);
        return convertProjectEntityToResponse(updatedProject);
    }

    // Delete a project by UID
    public void deleteProject(UUID projectUid) {
        Project project = projectRepository.findByProjectUid(projectUid)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with UID: " + projectUid));
        projectRepository.delete(project);
    }

    // Convert ProjectCreationRequest to Project entity
    public Project convertRequestToEntity(ProjectCreationRequest projectCreationRequest) {
        Project project = new Project();
        project.setProjectUid(UUID.randomUUID());
        project.setName(projectCreationRequest.getName());
        project.setDescription(projectCreationRequest.getDescription());
        return project;
    }

    // Convert Project entity to ProjectResponse
    public ProjectResponse convertProjectEntityToResponse(Project project) {
        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setProjectUid(project.getProjectUid());
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
    public TodoItem convertTodoResponseToEntity(TodoItemResponse todoResponse) {
        TodoItem todoItem = new TodoItem();
        todoItem.setTodoUid(todoResponse.getTodoUid());
        todoItem.setDescription(todoResponse.getDescription());
        todoItem.setDueBy(todoResponse.getDueBy());
        todoItem.setComplete(todoResponse.getComplete());
        todoItem.setStatus(todoResponse.getStatus());
        return todoItem;
    }

    // Convert TodoItem entity to TodoItemResponse
    public TodoItemResponse convertTodoEntityToResponse(TodoItem todoItem) {
        TodoItemResponse todoResponse = new TodoItemResponse();
        todoResponse.setTodoUid(todoItem.getTodoUid());
        todoResponse.setDescription(todoItem.getDescription());
        todoResponse.setDueBy(todoItem.getDueBy());
        todoResponse.setComplete(todoItem.getComplete());
        todoResponse.setStatus(todoItem.getStatus());
        return todoResponse;
    }
}
