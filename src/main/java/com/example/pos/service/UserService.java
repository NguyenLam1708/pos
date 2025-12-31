package com.example.pos.service;

import com.example.pos.dto.request.CreateUserRequest;
import com.example.pos.entitiy.user.User;
import io.smallrye.mutiny.Uni;

public interface UserService {
    public Uni<User> createUser(CreateUserRequest req);
}
