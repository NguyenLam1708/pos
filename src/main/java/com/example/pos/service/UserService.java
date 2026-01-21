package com.example.pos.service;

import com.example.pos.dto.request.ChangePasswordRequest;
import com.example.pos.dto.request.CreateUserRequest;
import com.example.pos.dto.request.UpdateUserRequest;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.UserResponse;
import com.example.pos.enums.user.UserStatus;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface UserService {
    //USER
    Uni<UserResponse> getMyInfo();
    Uni<UserResponse> changePassword(ChangePasswordRequest req);
    Uni<UserResponse> updateInfo(UpdateUserRequest req);

    // ADMIN
    Uni<UserResponse> createUser(CreateUserRequest req);
    Uni<UserResponse> getUserById(UUID id);
    Uni<UserResponse> updateUser(UUID id, UpdateUserRequest req);
    Uni<UserResponse> banUser(UUID id);
    Uni<UserResponse> activeUser(UUID id);
    Uni<PageResponse<UserResponse>> getUsers(int page, int size);

}
