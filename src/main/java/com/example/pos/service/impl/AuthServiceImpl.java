package com.example.pos.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.pos.dto.request.LoginRequest;
import com.example.pos.dto.response.LoginResponse;
import com.example.pos.enums.user.UserStatus;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.UserRepository;
import com.example.pos.repository.UserRoleRepository;
import com.example.pos.service.AuthService;
import com.example.pos.service.JwtService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserRoleRepository userRoleRepository;

    @Inject
    JwtService jwtService;

    public Uni<LoginResponse> login(LoginRequest req) {

        return userRepository.findByEmail(req.getEmail())
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "Email not found"))
                .flatMap(user -> {

                    // 🚫 Chặn user không ACTIVE
                    if (user.getStatus() != UserStatus.ACTIVE) {
                        return Uni.createFrom().failure(
                                new BusinessException(403, "User is not active")
                        );
                    }

                    var result = BCrypt.verifyer()
                            .verify(req.getPassword().toCharArray(), user.getPassword());

                    if (!result.verified) {
                        log.warn("Login failed for email: {}", req.getEmail());
                        return Uni.createFrom().failure(
                                new BusinessException(401, "Invalid credentials")
                        );
                    }

                    return userRoleRepository.findRoleCodesByUserId(user.getId())
                            .map(roles -> {

                                log.info("Roles from DB: {}", roles);

                                String token = jwtService.generateToken(
                                        user.getId(),
                                        user.getEmail(),
                                        roles,
                                        user.getPhone()
                                );

                                return new LoginResponse(token, user.getId());
                            });
                });
    }
}
