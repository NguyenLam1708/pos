package com.example.pos.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "app.jwt.secret")
    String secret;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "app.jwt.expiration-ms")
    long expirationMs;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String generateToken(String userId, String email, List<String> roleCodes, String phoneNumber) {
        Instant now = Instant.now();

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(userId)                       // sub
                .withIssuedAt(Date.from(now))              // iat
                .withExpiresAt(Date.from(now.plusMillis(expirationMs))) // exp
                .withClaim("email", email)
                .withClaim("phoneNumber", phoneNumber)
                .withArrayClaim("groups", roleCodes.toArray(new String[0])) // roles
                .sign(algorithm());
    }
}
