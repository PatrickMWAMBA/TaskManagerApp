package com.taskmanager.app.todo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
	
    List<TodoItem> getAllTasksByTaskUserId(Long userId);
    
    List<TodoItem> findByProjectId(Long projectId);  // Add this method to get tasks by project

    List<TodoItem> findByDueDateBetween(LocalDateTime start, LocalDateTime end);



}
