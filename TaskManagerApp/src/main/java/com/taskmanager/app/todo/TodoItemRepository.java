package com.taskmanager.app.todo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
	
    List<TodoItem> getAllTasksByTaskUserId(Long userId);


}
