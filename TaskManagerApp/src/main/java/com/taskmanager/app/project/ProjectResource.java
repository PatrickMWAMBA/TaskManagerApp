package com.taskmanager.app.project;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectResource {

    private final ProjectService projectService;

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Create a new project
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectCreationRequest projectCreationRequest) {
        ProjectResponse createdProject = projectService.createProject(projectCreationRequest);
        return ResponseEntity.ok(createdProject);
    }

    // Get a project by ID
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long projectId) {
        ProjectResponse projectResponse = projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectResponse);
    }

    // Get all projects
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    // Update a project
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @RequestBody ProjectResponse updatedResponse) {
        ProjectResponse updatedProject = projectService.updateProject(projectId, updatedResponse);
        return ResponseEntity.ok(updatedProject);
    }

    // Delete a project
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
