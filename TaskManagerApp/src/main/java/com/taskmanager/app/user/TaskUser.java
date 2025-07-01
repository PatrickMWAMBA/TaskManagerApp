package com.taskmanager.app.user;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmanager.app.role.Role;
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
    
    private UUID userUid;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(nullable = false, name = "date_created")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "date_updated")
    private OffsetDateTime updatedAt;

    @JsonIgnore  // prevents recursion during JSON serialization
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoItem> todoItems = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
            name = "UserRoles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles;

    
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
