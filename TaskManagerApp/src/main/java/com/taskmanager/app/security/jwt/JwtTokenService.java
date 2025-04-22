package com.taskmanager.app.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class JwtTokenService {

    private static final Duration JWT_TOKEN_VALIDITY = Duration.ofMinutes(30);

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenService(@Value("${jwt.secret}") String secretKey) {
        this.algorithm = Algorithm.HMAC512(secretKey);
        this.verifier = JWT.require(this.algorithm).build(); // ✅ no need to inject
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withArrayClaim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .withIssuer("app")
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(JWT_TOKEN_VALIDITY.toMillis()))
                .sign(algorithm);
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            return verifier.verify(token).getSubject();
        } catch (JWTVerificationException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return null;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = validateTokenAndGetUsername(token);
        return username != null && username.equals(userDetails.getUsername());
    }
}
