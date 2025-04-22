package com.taskmanager.app.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.taskmanager.app.BaseIT;

import java.util.List;

@SpringBootTest
@Transactional
class ProjectServiceTest extends BaseIT {

    @Autowired
    private ProjectService underTest;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveProject() {
        // Given
        ProjectCreationRequest request = new ProjectCreationRequest();
        request.setName("Test Project");
        request.setDescription("A sample project");

        // When
        ProjectResponse created = underTest.createProject(request);

        // Then
        ProjectResponse retrieved = underTest.getProjectById(created.getId());
        assertThat(retrieved.getName()).isEqualTo("Test Project");
        assertThat(retrieved.getDescription()).isEqualTo("A sample project");
    }

    @Test
    void shouldReturnAllProjects() {
        // Given
        ProjectCreationRequest request1 = new ProjectCreationRequest();
        request1.setName("Project One");
        request1.setDescription("Description One");

        ProjectCreationRequest request2 = new ProjectCreationRequest();
        request2.setName("Project Two");
        request2.setDescription("Description Two");

        underTest.createProject(request1);
        underTest.createProject(request2);

        // When
        List<ProjectResponse> allProjects = underTest.getAllProjects();

        // Then
        assertThat(allProjects).hasSize(2);
        assertThat(allProjects).extracting("name").contains("Project One", "Project Two");
    }

    @Test
    void shouldUpdateProject() {
        // Given
        ProjectCreationRequest request = new ProjectCreationRequest();
        request.setName("Initial Project");
        request.setDescription("Initial Description");

        ProjectResponse created = underTest.createProject(request);

        ProjectResponse updateRequest = new ProjectResponse();
        updateRequest.setId(created.getId());
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");

        // When
        ProjectResponse updated = underTest.updateProject(created.getId(), updateRequest);

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Project");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void shouldDeleteProject() {
        // Given
        ProjectCreationRequest request = new ProjectCreationRequest();
        request.setName("ToDelete Project");
        request.setDescription("Will be deleted");

        ProjectResponse created = underTest.createProject(request);

        // When
        underTest.deleteProject(created.getId());

        // Then
        assertThatThrownBy(() -> underTest.getProjectById(created.getId()))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining("Project not found with id");
    }
}
