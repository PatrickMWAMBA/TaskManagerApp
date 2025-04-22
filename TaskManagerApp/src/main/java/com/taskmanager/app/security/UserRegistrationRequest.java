package com.taskmanager.app.security;

import com.taskmanager.app.user.UserEmailUnique;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegistrationRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    @UserEmailUnique(message = "{registration.register.taken}")
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;
    
}
