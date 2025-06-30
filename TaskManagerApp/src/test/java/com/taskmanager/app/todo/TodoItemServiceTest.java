package com.taskmanager.app.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.taskmanager.app.BaseIT;
import com.taskmanager.app.project.Project;
import com.taskmanager.app.project.ProjectRepository;
import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class TodoItemServiceTest extends BaseIT {

	@Autowired
	private TodoItemService underTest;

	@Autowired
	private TaskUserRepository taskUserRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private TodoItemRepository todoItemRepository;

	@BeforeEach
	@Transactional
	void cleanDatabase() {
	    // Touch the EntityManager to force schema creation
	    todoItemRepository.findAll();

	    todoItemRepository.deleteAllInBatch();
	    projectRepository.deleteAllInBatch();
	    taskUserRepository.deleteAllInBatch();
	}

	@Test
	void shouldCreateAndRetrieveTodoItemWithProject() {
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
		request.setTaskName("Task Name");
		request.setDescription("Test Todo Item");
		request.setStartDate(LocalDateTime.now());
		request.setDueBy(LocalDateTime.now().plusDays(1));
		request.setUser(taskUser.getUserUid());
		request.setProject(project.getProjectUid());
		request.setPriority(PriorityLevel.HIGH);

		// When
		TodoItemResponse created = underTest.create(request);

		// Then
		TodoItemResponse retrieved = underTest.getByUid(created.getTodoUid());
		assertThat(retrieved.getDescription()).isEqualTo("Test Todo Item");
		assertThat(retrieved.getProject().getName()).isEqualTo("Test Project");
	}

	@Test
	void shouldReturnAllTodoItemsForProject() {
		// Given
		TaskUser user = new TaskUser();
		user.setUsername("Test User");
		user.setEmail("test@example.com");
		user.setPassword("password");
		user = taskUserRepository.save(user);

		Project project = new Project();
		project.setName("Test Project");
		project.setDescription("Project Description");
		project = projectRepository.save(project);

		TodoItemCreationRequest request1 = new TodoItemCreationRequest();
		request1.setTaskName("Task 1");
		request1.setDescription("Todo 1");
		request1.setStartDate(LocalDateTime.now());
		request1.setDueBy(LocalDateTime.now().plusDays(1));
		request1.setUser(user.getUserUid());
		request1.setProject(project.getProjectUid());

		TodoItemCreationRequest request2 = new TodoItemCreationRequest();
		request2.setTaskName("Task 2");
		request2.setDescription("Todo 2");
		request2.setStartDate(LocalDateTime.now());
		request2.setDueBy(LocalDateTime.now().plusDays(2));
		request2.setUser(user.getUserUid());
		request2.setProject(project.getProjectUid());

		underTest.create(request1);
		underTest.create(request2);

		// When
		List<TodoItemResponse> allTodos = underTest.getAllTasksForProject(project.getProjectUid());

		// Then
		assertThat(allTodos).hasSize(2);
		assertThat(allTodos).extracting("description").contains("Todo 1", "Todo 2");
	}

	@Test
	void shouldReturnAllTasksForUser() {
		// Given
		TaskUser user = new TaskUser();
		user.setUsername("Test User");
		user.setEmail("test@example.com");
		user.setPassword("password");
		user = taskUserRepository.save(user);

		Project project = new Project();
		project.setName("Test Project");
		project.setDescription("Project Description");
		project = projectRepository.save(project);

		TodoItemCreationRequest request1 = new TodoItemCreationRequest();
		request1.setTaskName("Task 1");
		request1.setDescription("Todo 1");
		request1.setStartDate(LocalDateTime.now());
		request1.setDueBy(LocalDateTime.now().plusDays(1));
		request1.setUser(user.getUserUid());
		request1.setProject(project.getProjectUid());

		TodoItemCreationRequest request2 = new TodoItemCreationRequest();
		request2.setTaskName("Task 2");
		request2.setDescription("Todo 2");
		request2.setStartDate(LocalDateTime.now());
		request2.setDueBy(LocalDateTime.now().plusDays(2));
		request2.setUser(user.getUserUid());
		request2.setProject(project.getProjectUid());

		underTest.create(request1);
		underTest.create(request2);

		// When
		List<TodoItemResponse> allTasksForUser = underTest.getAllTasksByUserUid(user.getUserUid());

		// Then
		assertThat(allTasksForUser).hasSize(2);
		assertThat(allTasksForUser).extracting("description").contains("Todo 1", "Todo 2");
	}

	@Test
	void shouldUpdateTodoItem() {
		// Given
		TaskUser user = new TaskUser();
		user.setUsername("Test User");
		user.setEmail("test@example.com");
		user.setPassword("password");
		user = taskUserRepository.save(user);

		Project project = new Project();
		project.setName("Test Project");
		project.setDescription("Project Description");
		project = projectRepository.save(project);

		TodoItemCreationRequest request = new TodoItemCreationRequest();
		request.setTaskName("Initial Task");
		request.setDescription("Todo 1");
		request.setStartDate(LocalDateTime.now());
		request.setDueBy(LocalDateTime.now().plusDays(1));
		request.setUser(user.getUserUid());
		request.setProject(project.getProjectUid());

		TodoItemResponse created = underTest.create(request);

		// When
		created.setDescription("Updated Todo");
		created.setComplete(true);
		created.setDueBy(LocalDateTime.now().plusDays(3));
		created.setStatus(TodoStatus.COMPLETE);

		TodoItemResponse updated = underTest.update(created.getTodoUid(), created);

		// Then
		assertThat(updated.getDescription()).isEqualTo("Updated Todo");
		assertThat(updated.getComplete()).isTrue();
		assertThat(updated.getStatus()).isEqualTo(TodoStatus.COMPLETE);
	}

	@Test
	void shouldDeleteTodoItem() {
		// Given
		TaskUser user = new TaskUser();
		user.setUsername("Test User");
		user.setEmail("test@example.com");
		user.setPassword("password");
		user = taskUserRepository.save(user);

		Project project = new Project();
		project.setName("Test Project");
		project.setDescription("Project Description");
		project = projectRepository.save(project);

		TodoItemCreationRequest request = new TodoItemCreationRequest();
		request.setTaskName("Task to Delete");
		request.setDescription("Todo 1");
		request.setStartDate(LocalDateTime.now());
		request.setDueBy(LocalDateTime.now().plusDays(1));
		request.setUser(user.getUserUid());
		request.setProject(project.getProjectUid());

		TodoItemResponse created = underTest.create(request);

		// When
		underTest.delete(created.getTodoUid());

		// Then
		assertThatThrownBy(() -> underTest.getByUid(created.getTodoUid())).isInstanceOf(TodoItemNotFoundException.class)
				.hasMessageContaining("Todo item not found with Uid");
	}
}
