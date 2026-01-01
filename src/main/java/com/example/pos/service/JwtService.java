package com.example.pos.service;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class JwtService {

    public String generateToken(
            UUID userId,
            String email,
            List<String> roles,
            String phoneNumber
    ) {

        return Jwt.issuer("pos-app")
                .subject(userId.toString())
                .claim("email", email)
                .claim("phoneNumber", phoneNumber)
                .groups(Set.copyOf(roles))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .sign();
    }
}
