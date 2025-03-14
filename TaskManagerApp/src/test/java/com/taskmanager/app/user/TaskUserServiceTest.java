package com.taskmanager.app.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
        request.setRoles(List.of(1L, 2L)); // Assign roles

        // When
        UserCreationResponse created = underTest.create(request);

        // Then
        UserCreationResponse retrieved = underTest.get(created.getId());
        assertThat(retrieved.getUsername()).isEqualTo("TestUser");
        assertThat(retrieved.getRoles()).containsExactlyInAnyOrder(1L, 2L); // Verify roles
    }

    @Test
    void shouldReturnAllTaskUsers() {
        // Given
        UserCreationRequest request1 = new UserCreationRequest();
        request1.setUsername("User1");
        request1.setEmail("user1@example.com");
        request1.setPassword("password");
        request1.setRoles(List.of(1L));

        UserCreationRequest request2 = new UserCreationRequest();
        request2.setUsername("User2");
        request2.setEmail("user2@example.com");
        request2.setPassword("password");
        request2.setRoles(List.of(2L));

        underTest.create(request1);
        underTest.create(request2);

        // When
        List<UserCreationResponse> allUsers = underTest.getAll();

        // Then
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers).extracting("username").contains("User1", "User2");
        assertThat(allUsers).flatExtracting("roles").contains(1L, 2L);
    }

    @Test
    void shouldUpdateTaskUser() {
        // Given
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("OriginalUser");
        request.setEmail("original@example.com");
        request.setPassword("password");
        request.setRoles(List.of(1L));

        UserCreationResponse created = underTest.create(request);

        UserCreationResponse updateRequest = new UserCreationResponse();
        updateRequest.setId(created.getId());
        updateRequest.setUsername("UpdatedUser");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setRoles(List.of(1L,2L)); // Update roles

        // When
        UserCreationResponse updated = underTest.update(created.getId(), updateRequest);

        // Then
        assertThat(updated.getUsername()).isEqualTo("UpdatedUser");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getRoles()).containsExactlyInAnyOrder(1L,2L); // Verify updated roles
    }

    @Test
    void shouldDeleteTaskUser() {
        // Given
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("ToDeleteUser");
        request.setEmail("delete@example.com");
        request.setPassword("password");
        request.setRoles(List.of(1L, 2L));

        UserCreationResponse created = underTest.create(request);

        // When
        underTest.delete(created.getId());

        // Then
        assertThatThrownBy(() -> underTest.get(created.getId()))
                .isInstanceOf(TaskUserNotFoundException.class)
                .hasMessageContaining("User not found with id");
    }
    
    @Test
    void shouldReturnAllSuperAdmins() {
        // Given
        UserCreationRequest request1 = new UserCreationRequest();
        request1.setUsername("SuperAdmin1");
        request1.setEmail("superadmin1@example.com");
        request1.setPassword("password");
        request1.setRoles(List.of(1L)); // Super Admin

        UserCreationRequest request2 = new UserCreationRequest();
        request2.setUsername("SuperAdmin2");
        request2.setEmail("superadmin2@example.com");
        request2.setPassword("password");
        request2.setRoles(List.of(1L)); // Super Admin

        UserCreationRequest request3 = new UserCreationRequest();
        request3.setUsername("Admin1");
        request3.setEmail("admin1@example.com");
        request3.setPassword("password");
        request3.setRoles(List.of(2L)); // Admin

        underTest.create(request1);
        underTest.create(request2);
        underTest.create(request3);

        // When
        List<UserCreationResponse> superAdmins = underTest.getUsersByRole(1L);

        // Then
        assertThat(superAdmins).hasSize(2);
        assertThat(superAdmins).extracting("username").containsExactlyInAnyOrder("SuperAdmin1", "SuperAdmin2");
    }

    @Test
    void shouldReturnAllAdmins() {
        // Given
        UserCreationRequest request1 = new UserCreationRequest();
        request1.setUsername("Admin1");
        request1.setEmail("admin1@example.com");
        request1.setPassword("password");
        request1.setRoles(List.of(2L)); // Admin

        UserCreationRequest request2 = new UserCreationRequest();
        request2.setUsername("Admin2");
        request2.setEmail("admin2@example.com");
        request2.setPassword("password");
        request2.setRoles(List.of(2L)); // Admin

        UserCreationRequest request3 = new UserCreationRequest();
        request3.setUsername("SuperAdmin1");
        request3.setEmail("superadmin1@example.com");
        request3.setPassword("password");
        request3.setRoles(List.of(1L)); // Super Admin

        underTest.create(request1);
        underTest.create(request2);
        underTest.create(request3);

        // When
        List<UserCreationResponse> admins = underTest.getUsersByRole(2L);

        // Then
        assertThat(admins).hasSize(2);
        assertThat(admins).extracting("username").containsExactlyInAnyOrder("Admin1", "Admin2");
    }
    
    @Test
    void shouldFindUserByEmail() {
        // Given
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("EmailUser");
        request.setEmail("emailuser@example.com");
        request.setPassword("password");
        request.setRoles(List.of(1L));

        UserCreationResponse created = underTest.create(request);

        // When
        UserCreationResponse retrieved = underTest.findByEmail("emailuser@example.com");

        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getEmail()).isEqualTo("emailuser@example.com");
        assertThat(retrieved.getUsername()).isEqualTo("EmailUser");
    }


}
