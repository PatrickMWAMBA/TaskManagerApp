package com.taskmanager.app.project;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project API", description = "Endpoints for managing projects")
public class ProjectResource {

    private final ProjectService projectService;

    public ProjectResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Create a new project")
    @ApiResponse(responseCode = "200", description = "Project successfully created")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectCreationRequest projectCreationRequest) {
        ProjectResponse createdProject = projectService.createProject(projectCreationRequest);
        return ResponseEntity.ok(createdProject);
    }

    @Operation(summary = "Get a project by UID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Project found"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/{projectUid}")
    public ResponseEntity<ProjectResponse> getProjectByUid(@PathVariable UUID projectUid) {
        ProjectResponse projectResponse = projectService.getProjectByUid(projectUid);
        return ResponseEntity.ok(projectResponse);
    }

    @Operation(summary = "Get all projects")
    @ApiResponse(responseCode = "200", description = "List of all projects returned")
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Update a project by UID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Project successfully updated"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @PutMapping("/{projectUid}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID projectUid,
            @RequestBody ProjectResponse updatedResponse) {

        ProjectResponse updatedProject = projectService.updateProject(projectUid, updatedResponse);
        return ResponseEntity.ok(updatedProject);
    }

    @Operation(summary = "Delete a project by UID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Project successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @DeleteMapping("/{projectUid}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectUid) {
        projectService.deleteProject(projectUid);
        return ResponseEntity.noContent().build();
    }
}
