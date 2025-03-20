package com.taskmanager.app.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.taskmanager.app.project.Project;
import com.taskmanager.app.user.TaskUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "to_do_items")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@EnableJpaAuditing
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean complete = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoStatus status = TodoStatus.PENDING; // Default status is PENDING

    @CreatedDate
    @Column(nullable = false, name = "date_created")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "date_updated")
    private OffsetDateTime updatedAt;

    @FutureOrPresent
    @Column(nullable = false, name = "due_by")
    private LocalDateTime dueBy;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private TaskUser taskUser;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    
    @Override
    public String toString() {
        return "TodoItem{" +
               "id=" + id +
               ", description='" + description + '\'' +
               // Avoid calling TaskUser's toString directly if it's included
               ", user=" + (taskUser != null ? taskUser.getId() : null) + 
               '}';
    }

}
