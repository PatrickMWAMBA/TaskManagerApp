package com.taskmanager.app.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class TaskUserServiceTest {

    @Autowired
    private TaskUserService underTest;

    @Autowired
    private TaskUserRepository taskUserRepository;

    @BeforeEach
    void setUp() {
        taskUserRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveTaskUser() {
        // Given
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("TestUser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        // When
        UserCreationResponse created = underTest.create(request);

        // Then
        UserCreationResponse retrieved = underTest.get(created.getId());
        assertThat(retrieved.getUsername()).isEqualTo("TestUser");
    }

    @Test
    void shouldReturnAllTaskUsers() {
        // Given
        UserCreationRequest request1 = new UserCreationRequest();
        request1.setUsername("User1");
        request1.setEmail("user1@example.com");
        request1.setPassword("password");

        UserCreationRequest request2 = new UserCreationRequest();
        request2.setUsername("User2");
        request2.setEmail("user2@example.com");
        request2.setPassword("password");

        underTest.create(request1);
        underTest.create(request2);

        // When
        List<UserCreationResponse> allUsers = underTest.getAll();

        // Then
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).extracting("username").contains("User1", "User2");
    }

    @Test
    void shouldUpdateTaskUser() {
        // Given
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("OriginalUser");
        request.setEmail("original@example.com");
        request.setPassword("password");
        
        UserCreationResponse created = underTest.create(request);
        
        UserCreationResponse updateRequest = new UserCreationResponse();
        updateRequest.setId(created.getId());
        updateRequest.setUsername("UpdatedUser");
        updateRequest.setEmail("updated@example.com");
        
        // When
        UserCreationResponse updated = underTest.update(created.getId(), updateRequest);
        
        // Then
        assertThat(updated.getUsername()).isEqualTo("UpdatedUser");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void shouldDeleteTaskUser() {
        // Given
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("ToDeleteUser");
        request.setEmail("delete@example.com");
        request.setPassword("password");
        
        UserCreationResponse created = underTest.create(request);

        // When
        underTest.delete(created.getId());

        // Then
        assertThatThrownBy(() -> underTest.get(created.getId()))
                .isInstanceOf(TaskUserNotFoundException.class)
                .hasMessageContaining("User not found with id");
    }


}
