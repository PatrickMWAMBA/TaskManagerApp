package com.taskmanager.app.comment;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.taskmanager.app.todo.TodoItem;
import com.taskmanager.app.user.TaskUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, name = "uid")
  private UUID commentUid;


  @Column(nullable = false, length = 1000)
  private String content;

  @CreatedDate
  private OffsetDateTime createdAt;

  @LastModifiedDate
  private OffsetDateTime lastUpdatedAt;

  @ManyToOne
  @JoinColumn(name = "todo_id", nullable = false)
  private TodoItem todoItem;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private TaskUser author;
  
  

}
