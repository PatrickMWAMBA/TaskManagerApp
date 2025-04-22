package com.taskmanager.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
public class TaskUserDetailsService implements UserDetailsService {
	
	private final TaskUserRepository taskUserRepository;
	
	@Autowired
	public TaskUserDetailsService(TaskUserRepository taskUserRepository) {
		super();
		this.taskUserRepository = taskUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
        final TaskUser user = taskUserRepository.findByEmailIgnoreCase(username);
        if (user == null) {
            log.warn("user not found: {}", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }

		return new TaskUserDetails(user);
	}

}
