package com.example.pos.service;

import com.example.pos.dto.LoginRequest;
import com.example.pos.dto.response.AuthResponse;
import io.smallrye.mutiny.Uni;

public interface AuthService {
    Uni<AuthResponse> login(LoginRequest loginRequest);
}
