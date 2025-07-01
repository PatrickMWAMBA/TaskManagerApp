package com.taskmanager.app.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskUserRepository extends JpaRepository<TaskUser, Long> {
	
	List<TaskUser> findByRolesId(Long roleId);
	
	Optional<TaskUser> findByEmail(String email);
	
    @EntityGraph(attributePaths = "roles")
    TaskUser findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
	
	@EntityGraph(attributePaths = {"todoItems", "roles"})
	Optional<TaskUser> findByUserUid(UUID userUid);
    
    boolean existsByUserUid(UUID userUid);






}
