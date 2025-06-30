package com.taskmanager.app.project;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmanager.app.todo.TodoItem;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project>findByProjectUid(UUID projectUid);

}
