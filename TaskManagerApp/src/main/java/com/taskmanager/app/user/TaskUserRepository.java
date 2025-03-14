package com.taskmanager.app.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskUserRepository extends JpaRepository<TaskUser, Long> {
	
	List<TaskUser> findByRolesId(Long roleId);
	
	Optional<TaskUser> findByEmail(String email);



}
