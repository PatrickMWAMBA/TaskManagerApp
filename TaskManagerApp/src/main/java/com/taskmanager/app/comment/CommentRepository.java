package com.taskmanager.app.comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	// Find all comments for a specific todo item
	List<Comment> findAllByTodoItem_TodoUid(UUID projectUid);

	List<Comment> findByAuthor_UserUid(UUID userUid);

	// Find a single comment by its unique commentUid
	Optional<Comment> findByCommentUid(UUID commentUid);
}