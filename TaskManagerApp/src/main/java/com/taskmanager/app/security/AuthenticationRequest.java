package com.taskmanager.app.security;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthenticationRequest {

    @NotNull
    @Size(max = 255)
    private String email;

    @NotNull
    @Size(max = 72)
    private String password;

}
