package com.taskmanager.app.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.taskmanager.app.project.Project;
import com.taskmanager.app.project.ProjectRepository;
import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserRepository;
import com.taskmanager.app.user.UserCreationResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
class TodoItemServiceTest {

    @Autowired
    private TodoItemService underTest;

    @Autowired
    private TaskUserRepository taskUserRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TodoItemRepository todoItemRepository;
    
    @BeforeEach
    void setUp() {
        todoItemRepository.deleteAll();
        taskUserRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveTodoItemWithProject() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");
        taskUser.setPassword("password");
        taskUser = taskUserRepository.save(taskUser);

        // Create a Project
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Project Description");
        project = projectRepository.save(project);

        TodoItemCreationRequest request = new TodoItemCreationRequest();
        request.setDescription("Test Todo Item");
        request.setDueBy(LocalDateTime.now().plusDays(1));
        request.setUser(taskUser.getId());
        request.setProject(project.getId()); // Set the project association

        // When
        TodoItemResponse created = underTest.create(request);

        // Then
        TodoItemResponse retrieved = underTest.get(created.getId());
        assertThat(retrieved.getDescription()).isEqualTo("Test Todo Item");
        assertThat(retrieved.getProject().getName()).isEqualTo("Test Project"); // Check the project association
    }

    @Test
    void shouldReturnAllTodoItemsForProject() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");
        taskUser.setPassword("password");
        taskUser = taskUserRepository.save(taskUser);

        // Create a Project
        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Project Description");

        project = projectRepository.save(project);

        TodoItemCreationRequest request1 = new TodoItemCreationRequest();
        request1.setDescription("Todo 1");
        request1.setDueBy(LocalDateTime.now().plusDays(1));
        request1.setUser(taskUser.getId());
        request1.setProject(project.getId()); // Associate with the project

        TodoItemCreationRequest request2 = new TodoItemCreationRequest();
        request2.setDescription("Todo 2");
        request2.setDueBy(LocalDateTime.now().plusDays(2));
        request2.setUser(taskUser.getId());
        request2.setProject(project.getId());

        underTest.create(request1);
        underTest.create(request2);

        // When
        List<TodoItemResponse> allTodos = underTest.getAllTasksForProject(project.getId()); // This is the new method name

        // Then
        assertThat(allTodos).hasSize(2);
        assertThat(allTodos).extracting("description").contains("Todo 1", "Todo 2");
    }

    @Test
    void shouldReturnAllTasksForUser() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");
        taskUser.setPassword("password");
        taskUser = taskUserRepository.save(taskUser);

        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Project Description");
        project = projectRepository.save(project);

        TodoItemCreationRequest request1 = new TodoItemCreationRequest();
        request1.setDescription("Todo 1");
        request1.setDueBy(LocalDateTime.now().plusDays(1));
        request1.setUser(taskUser.getId());
        request1.setProject(project.getId());

        TodoItemCreationRequest request2 = new TodoItemCreationRequest();
        request2.setDescription("Todo 2");
        request2.setDueBy(LocalDateTime.now().plusDays(2));
        request2.setUser(taskUser.getId());
        request2.setProject(project.getId());

        underTest.create(request1);
        underTest.create(request2);

        // When
        List<TodoItemResponse> allTasksForUser = underTest.getAllTasksByUserId(taskUser.getId()); // Get all tasks for user

        // Then
        assertThat(allTasksForUser).hasSize(2);
        assertThat(allTasksForUser).extracting("description").contains("Todo 1", "Todo 2");
    }

    @Test
    void shouldUpdateTodoItem() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");
        taskUser.setPassword("password");
        taskUser = taskUserRepository.save(taskUser);

        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Project Description");
        project = projectRepository.save(project);

        TodoItemCreationRequest request = new TodoItemCreationRequest();
        request.setDescription("Todo 1");
        request.setDueBy(LocalDateTime.now().plusDays(1));
        request.setUser(taskUser.getId());
        request.setProject(project.getId());

        TodoItemResponse createdTodoItem = underTest.create(request);

        // When updating the todo item
        createdTodoItem.setDescription("Updated Todo");
        createdTodoItem.setComplete(true);
        createdTodoItem.setDueBy(LocalDateTime.now().plusDays(3));
        createdTodoItem.setStatus(TodoStatus.COMPLETE);

        TodoItemResponse updatedTodoItem = underTest.update(createdTodoItem.getId(), createdTodoItem);

        // Then
        assertThat(updatedTodoItem.getDescription()).isEqualTo("Updated Todo");
        assertThat(updatedTodoItem.getComplete()).isTrue();
        assertThat(updatedTodoItem.getStatus()).isEqualTo(TodoStatus.COMPLETE);
    }

    @Test
    void shouldDeleteTodoItem() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");
        taskUser.setPassword("password");
        taskUser = taskUserRepository.save(taskUser);

        Project project = new Project();
        project.setName("Test Project");
        project.setDescription("Project Description");
        project = projectRepository.save(project);

        TodoItemCreationRequest request = new TodoItemCreationRequest();
        request.setDescription("Todo 1");
        request.setDueBy(LocalDateTime.now().plusDays(1));
        request.setUser(taskUser.getId());
        request.setProject(project.getId());

        TodoItemResponse createdTodoItem = underTest.create(request);

        // When
        underTest.delete(createdTodoItem.getId());

        // Then
        assertThatThrownBy(() -> underTest.get(createdTodoItem.getId()))
                .isInstanceOf(TodoItemNotFoundException.class)
                .hasMessageContaining("Todo item not found with id");
    }
}
