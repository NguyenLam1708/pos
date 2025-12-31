package com.example.pos.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.pos.dto.request.CreateUserRequest;
import com.example.pos.entitiy.user.User;
import com.example.pos.entitiy.user.UserRole;
import com.example.pos.enums.user.UserStatus;
import com.example.pos.exception.BusinessException;
import com.example.pos.reponsitory.RoleRepository;
import com.example.pos.reponsitory.UserRepository;
import com.example.pos.reponsitory.UserRoleRepository;
import com.example.pos.service.UserService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    UserRoleRepository userRoleRepository;

    @Override
    @WithTransaction
    public Uni<User> createUser(CreateUserRequest req) {

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            return Uni.createFrom()
                    .failure(new BusinessException(400, "Password confirmation does not match"));
        }

        return userRepository.existsByEmail(req.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Uni.createFrom()
                                .failure(new BusinessException(400, "Email address already in use"));
                    }

                    String hashedPassword =
                            BCrypt.withDefaults()
                                    .hashToString(12, req.getPassword().toCharArray());

                    User user = User.builder()
                            .email(req.getEmail())
                            .phone(req.getPhone())
                            .firstName(req.getFirstName())
                            .lastName(req.getLastName())
                            .fullName(req.getFullName())
                            .username(req.getUsername())
                            .password(hashedPassword)
                            .status(UserStatus.ACTIVE)
                            .build();

                    return userRepository.persist(user)
                            .flatMap(savedUser ->
                                    // 2️⃣ lấy role STAFF
                                    roleRepository.findByCode("USER")
                                            .onItem().ifNull()
                                            .failWith(() -> new BusinessException(500, "Default role USER not found"))
                                            .flatMap(role -> {

                                                // 3️⃣ gán role cho user
                                                UserRole userRole = new UserRole();
                                                userRole.setUserId(savedUser.getId());
                                                userRole.setRoleId(role.getId());

                                                return userRoleRepository.persist(userRole)
                                                        .replaceWith(savedUser);
                                            })
                            );
                });

    }


}
