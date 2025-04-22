package com.taskmanager.app.security;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthenticationResponse {

    private String jwtAccessToken;

}
