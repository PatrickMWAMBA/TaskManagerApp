package com.taskmanager.app.user;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.taskmanager.app.todo.TodoItem;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class TaskUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(nullable = false, name = "date_created")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "date_updated")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "taskUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoItem> todoItems = new ArrayList<>();
    
    @Override
    public String toString() {
        return "TaskUser{" +
               "id=" + id +
               ", name='" + username + '\'' +
               // Avoid calling TodoItem's toString directly
               ", todoItems=" + (todoItems != null ? todoItems.size() : null) + 
               '}';
    }

}
