package com.taskmanager.app.security;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.app.user.UserCreationResponse;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Registration", description = "user Registration endpoints")
@RestController
public class UserRegistrationResource {

  private final UserRegistrationService registrationService;

  public UserRegistrationResource(final UserRegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  //TODO: Lock this to only SUPER_ADMIN Role
  //@PreAuthorize("hasAnyAuthority('" + UserRoles.ROLE_SUPER_ADMIN + "')")
  @PostMapping("/register")
  public ResponseEntity<Void> register(
      @RequestBody @Valid final UserRegistrationRequest registrationRequest) {
    registrationService.register(registrationRequest);
    return ResponseEntity.ok().build();
  }
  
}