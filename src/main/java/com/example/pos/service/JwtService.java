package com.example.pos.service;

import com.auth0.jwt.algorithms.Algorithm;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class JwtService {

    private final Algorithm algorithm;

    public JwtService() {
        this.algorithm = Algorithm.RSA256(null, loadPrivateKey());
    }

    private RSAPrivateKey loadPrivateKey() {
        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("privateKey.pem");

            String key = new String(is.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return (RSAPrivateKey)
                    KeyFactory.getInstance("RSA").generatePrivate(spec);

        } catch (Exception e) {
            throw new RuntimeException("Cannot load private key", e);
        }
    }

    public String generateToken(
            String userId,
            String email,
            List<String> roles,
            String phoneNumber
    ) {
        Instant now = Instant.now();

        return com.auth0.jwt.JWT.create()
                .withIssuer("pos-app")
                .withSubject(userId)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(3600)))
                .withClaim("email", email)
                .withClaim("phoneNumber", phoneNumber)
                .withArrayClaim("groups", roles.toArray(new String[0]))
                .sign(algorithm);
    }
}
