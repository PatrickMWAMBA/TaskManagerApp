package com.taskmanager.app.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.taskmanager.app.security.jwt.JwtTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin
@Tag(name = "Authentication", description = "Authentication endpoints")
@RestController
@RequestMapping
public class AuthenticationResource {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenService jwtTokenService;
	private final UserDetailsService userDetailsService;

	public AuthenticationResource(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService,
			UserDetailsService userDetailsService) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenService = jwtTokenService;
		this.userDetailsService = userDetailsService;
	}

	@Operation(summary = "User login", description = "Authenticates user and returns a JWT token.")
	@PostMapping("/login")
	public AuthenticationResponse authenticateUser(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
		try {
			// Authenticate the user
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
					authenticationRequest.getPassword()));

			// Fetch authenticated user details
			UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

			// Generate token for authenticated user
			AuthenticationResponse authenticationResponse = new AuthenticationResponse();
			authenticationResponse.setJwtAccessToken(jwtTokenService.generateToken(userDetails));

			return authenticationResponse;
		} catch (BadCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
	}
}
