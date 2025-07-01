package com.taskmanager.app.todo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {

  
  List<TodoItem> findAllByUser_UserUid(UUID projectUid);
  
  List<TodoItem> findAllByProject_ProjectUid(UUID projectUid);

  List<TodoItem> findByDueByBetween(LocalDateTime start, LocalDateTime end);

  Optional<TodoItem> findByTodoUid(UUID todoUid);

  @Query("SELECT t FROM TodoItem t LEFT JOIN FETCH t.comments WHERE t.todoUid = :todoUid")
  Optional<TodoItem> findWithCommentsByTodoUid(@Param("todoUid") UUID todoUid);



}
