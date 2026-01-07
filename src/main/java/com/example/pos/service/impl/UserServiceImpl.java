package com.example.pos.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.pos.dto.request.ChangePasswordRequest;
import com.example.pos.dto.request.CreateUserRequest;
import com.example.pos.dto.request.UpdateUserRequest;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.UserResponse;
import com.example.pos.entitiy.user.User;
import com.example.pos.entitiy.user.UserRole;
import com.example.pos.enums.user.UserStatus;
import com.example.pos.exception.BusinessException;
import com.example.pos.reponsitory.RoleRepository;
import com.example.pos.reponsitory.UserRepository;
import com.example.pos.reponsitory.UserRoleRepository;
import com.example.pos.service.UserService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    UserRoleRepository userRoleRepository;

    @Inject
    SecurityIdentity identity;

    @Override
    @WithTransaction
    public Uni<UserResponse> createUser(CreateUserRequest req) {

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
                                    roleRepository.findByCode("USER")
                                            .onItem().ifNull()
                                            .failWith(() ->
                                                    new BusinessException(500, "Default role USER not found"))
                                            .flatMap(role -> {

                                                UserRole userRole = new UserRole();
                                                userRole.setUserId(savedUser.getId());
                                                userRole.setRoleId(role.getId());

                                                return userRoleRepository.persist(userRole)
                                                        .replaceWith(savedUser);
                                            })
                            )
                            .map(UserResponse::toUserResponse);
                });
    }

    @Override
    public Uni<UserResponse> getMyInfo() {
        UUID userId = UUID.fromString(identity.getPrincipal().getName());

        return userRepository.findById(userId)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "User not found"))
                .map(UserResponse::toUserResponse);
    }

    @Override
    @WithTransaction
    public Uni<UserResponse> changePassword(ChangePasswordRequest req) {

        UUID userId = UUID.fromString(identity.getPrincipal().getName());

        return userRepository.findById(userId)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "User not found"))
                .flatMap(user -> {

                    var verified = BCrypt.verifyer()
                            .verify(req.getOldPassword().toCharArray(), user.getPassword());

                    if (!verified.verified) {
                        return Uni.createFrom()
                                .failure(new BusinessException(400, "Old password is incorrect"));
                    }

                    if (!req.getNewPassword().equals(req.getConfirmPassword())) {
                        return Uni.createFrom()
                                .failure(new BusinessException(400, "Password confirmation does not match"));
                    }

                    if (req.getNewPassword().equals(req.getOldPassword())) {
                        return Uni.createFrom()
                                .failure(new BusinessException(400, "New password can not same old password"));
                    }
                    String newHashedPassword = BCrypt.withDefaults()
                            .hashToString(12, req.getNewPassword().toCharArray());

                    user.setPassword(newHashedPassword);

                    return userRepository.persist(user);
                })
                .map(UserResponse::toUserResponse);
    }

    @Override
    @WithTransaction
    public Uni<UserResponse> updateInfo(UpdateUserRequest req) {

        UUID userId = UUID.fromString(identity.getPrincipal().getName());

        return userRepository.findById(userId)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "User not found"))
                .flatMap(user -> {

                    user.setFirstName(req.getFirstName());
                    user.setLastName(req.getLastName());
                    user.setFullName(req.getFullName());
                    user.setPhone(req.getPhone());
                    user.setUsername(req.getUsername());

                    return userRepository.persist(user);
                })
                .map(UserResponse::toUserResponse);
    }

    @Override
    public Uni<UserResponse> getUserById(UUID id){
        return userRepository.findById(id)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "User Not Found"))
                .map(UserResponse::toUserResponse);
    }

    @Override
    @WithTransaction
    public Uni<UserResponse> updateUser(UUID id, UpdateUserRequest req) {
        return userRepository.findById(id)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "User Not Found"))
                .flatMap( user -> {

                    user.setFirstName(req.getFirstName());
                    user.setLastName(req.getLastName());
                    user.setFullName(req.getFullName());
                    user.setPhone(req.getPhone());
                    user.setUsername(req.getUsername());

                    return userRepository.persist(user);
                })
                .map(UserResponse::toUserResponse);
    }

    @Override
    @WithTransaction
    public Uni<UserResponse> banUser(UUID id) {
        return userRepository.findById(id)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "User Not Found"))
                .flatMap( user -> {

                    user.setStatus(UserStatus.BANNED);

                    return userRepository.persist(user);
                })
                .map(UserResponse::toUserResponse);
    }

    @Override
    @WithTransaction
    public Uni<UserResponse> activeUser(UUID id) {
        return userRepository.findById(id)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "User Not Found"))
                .flatMap( user -> {

                    user.setStatus(UserStatus.ACTIVE);

                    return userRepository.persist(user);
                })
                .map(UserResponse::toUserResponse);
    }

    @Override
    public Uni<PageResponse<UserResponse>> getUsers(int page, int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        var query = userRepository.findAll();

        Uni<Long> countUni = query.count();

        Uni<List<UserResponse>> itemsUni =
                query.page(Page.of(safePage, safeSize))
                        .list()
                        .map(list -> list.stream()
                                .map(UserResponse::toUserResponse)
                                .toList());

        return Uni.combine().all().unis(itemsUni, countUni)
                .asTuple()
                .map(tuple -> {
                    long totalItems = tuple.getItem2();
                    int totalPages = (int) Math.ceil((double) totalItems / safeSize);

                    return PageResponse.<UserResponse>builder()
                            .items(tuple.getItem1())
                            .page(safePage)
                            .size(safeSize)
                            .totalItems(totalItems)
                            .totalPages(totalPages)
                            .hasNext(safePage < totalPages - 1)
                            .hasPrevious(safePage > 0)
                            .build();
                });
    }
}
