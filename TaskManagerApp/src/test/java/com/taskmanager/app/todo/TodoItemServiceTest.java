package com.taskmanager.app.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserNotFoundException;
import com.taskmanager.app.user.TaskUserRepository;

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
    private TodoItemRepository todoItemRepository;
    
    @BeforeEach
    void setUp() {
        todoItemRepository.deleteAll();
        taskUserRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveTodoItem() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");  // Ensure email is set
        taskUser.setPassword("password");       // Ensure password is set
        taskUser = taskUserRepository.save(taskUser);

        TodoItemCreationRequest request = new TodoItemCreationRequest();
        request.setDescription("Test Todo Item");
        request.setDueBy(LocalDateTime.now().plusDays(1));
        request.setUser(taskUser.getId());

        // When
        TodoItemResponse created = underTest.create(request);

        // Then
        TodoItemResponse retrieved = underTest.get(created.getId());
        assertThat(retrieved.getDescription()).isEqualTo("Test Todo Item");
    }

    @Test
    @Transactional
    void shouldReturnAllTodoItems() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");  // Ensure email is set
        taskUser.setPassword("password");       // Ensure password is set
        
        taskUser = taskUserRepository.save(taskUser); // Save after setting required fields

        TodoItemCreationRequest request1 = new TodoItemCreationRequest();
        request1.setDescription("Todo 1");
        request1.setDueBy(LocalDateTime.now().plusDays(1));
        request1.setUser(taskUser.getId());

        TodoItemCreationRequest request2 = new TodoItemCreationRequest();
        request2.setDescription("Todo 2");
        request2.setDueBy(LocalDateTime.now().plusDays(2));
        request2.setUser(taskUser.getId());

        underTest.create(request1);
        underTest.create(request2);

        // When
        List<TodoItemResponse> allTodos = underTest.getAllTodoItems();

        // Then
        assertThat(allTodos).hasSize(2);
        assertThat(allTodos).extracting("description").contains("Todo 1", "Todo 2");
    }

    @Test
    void shouldDeleteTodoItem() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");  // Ensure email is set
        taskUser.setPassword("password");       // Ensure password is set
        taskUser = taskUserRepository.save(taskUser);

        TodoItemCreationRequest request = new TodoItemCreationRequest();
        request.setDescription("Delete Me");
        request.setDueBy(LocalDateTime.now().plusDays(1));
        request.setUser(taskUser.getId());
        
        TodoItemResponse created = underTest.create(request);

        // When
        underTest.delete(created.getId());

        // Then
        // Ensure the item is deleted and no longer retrievable
        assertThatThrownBy(() -> underTest.get(created.getId()))
                .isInstanceOf(TodoItemNotFoundException.class)
                .hasMessageContaining("Todo item not found with id");
    }

    @Test
    void shouldUpdateTodoItem() {
        // Given
        TaskUser taskUser = new TaskUser();
        taskUser.setUsername("Test User");
        taskUser.setEmail("test@example.com");  // Ensure email is set
        taskUser.setPassword("password");       // Ensure password is set
        taskUser = taskUserRepository.save(taskUser);

        TodoItemCreationRequest request = new TodoItemCreationRequest();
        request.setDescription("Original Todo");
        request.setDueBy(LocalDateTime.now().plusDays(1));
        request.setUser(taskUser.getId());

        TodoItemResponse created = underTest.create(request);

        // Ensure the created TodoItem is saved correctly
        assertThat(created.getDescription()).isEqualTo("Original Todo");

        // Create update request
        TodoItemResponse updateRequest = new TodoItemResponse();
        updateRequest.setId(created.getId());
        updateRequest.setDescription("Updated Todo");
        updateRequest.setDueBy(LocalDateTime.now().plusDays(3));
        updateRequest.setComplete(true);
        updateRequest.setStatus(TodoStatus.COMPLETE);
        updateRequest.setUser(taskUser.getId());  // Ensure user ID is set

        // When
        TodoItemResponse updated = underTest.update(created.getId(), updateRequest);

        // Then
        assertThat(updated.getDescription()).isEqualTo("Updated Todo");
        assertThat(updated.getStatus()).isEqualTo(TodoStatus.COMPLETE);
        assertThat(updated.getComplete()).isTrue();  // Ensure the 'complete' flag is correctly updated
        assertThat(updated.getDueBy()).isEqualTo(updateRequest.getDueBy());
    }
}
