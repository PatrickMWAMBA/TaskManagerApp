package com.taskmanager.app.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskmanager.app.role.RoleRepository;
import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserRegistrationService {

  private final TaskUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  public UserRegistrationService(final TaskUserRepository userRepository,
      final PasswordEncoder passwordEncoder, final RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.roleRepository = roleRepository;
  }

  public void register(final UserRegistrationRequest registrationRequest) {
    log.info("registering new user: {}", registrationRequest.getEmail());

    final TaskUser user = new TaskUser();
    user.setEmail(registrationRequest.getEmail());
    user.setUsername(registrationRequest.getUsername());
    user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
    // assign default role
    user.setRoles(Collections.singleton(roleRepository.findByName(UserRoles.ROLE_USER)));
    userRepository.save(user);
  }

}
