package com.example.pos.service;

import com.example.pos.dto.request.LoginRequest;
import com.example.pos.dto.response.LoginResponse;
import io.smallrye.mutiny.Uni;

public interface AuthService {
    Uni<LoginResponse> login(LoginRequest req);
}
