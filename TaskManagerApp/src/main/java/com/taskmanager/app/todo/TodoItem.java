package com.taskmanager.app.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.taskmanager.app.comment.Comment;
import com.taskmanager.app.project.Project;
import com.taskmanager.app.user.TaskUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.FutureOrPresent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "to_do_items")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@EnableJpaAuditing
public class TodoItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, columnDefinition = "uniqueidentifier", name = "uid")
  private UUID todoUid;

  @Column(nullable = false)
  private String taskName;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Boolean complete = false;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TodoStatus status = TodoStatus.PENDING;

  @CreatedDate
  @Column(nullable = false, name = "date_created")
  private OffsetDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false, name = "date_updated")
  private OffsetDateTime updatedAt;

  @FutureOrPresent
  @Column(nullable = false, name = "due_by")
  private LocalDateTime dueBy;

  // S @FutureOrPresent
  @Column(nullable = false, name = "start_date")
  private LocalDateTime startDate;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private TaskUser user;

  @ManyToOne
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PriorityLevel priority = PriorityLevel.MEDIUM;

  @OneToMany(mappedBy = "todoItem", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @Transient
  public int getTotalComments() {
    return comments != null ? comments.size() : 0;
  }

  @Override
  public String toString() {
    return "TodoItem{" + "id=" + id + ", taskName='" + taskName + '\'' + ", description='"
        + description + '\'' + ", totalComments=" + getTotalComments() + ", user="
        + (user != null ? user.getId() : null) + '}';
  }
}
