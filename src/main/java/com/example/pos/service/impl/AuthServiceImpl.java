package com.example.pos.service.impl;

import com.example.pos.dto.LoginRequest;
import com.example.pos.dto.response.AuthResponse;
import com.example.pos.reponsitories.RoleRepository;
import com.example.pos.reponsitories.UserRepository;
import com.example.pos.service.AuthService;
import com.example.pos.service.JwtService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    JwtService jwtService;


    @Override
    public Uni<AuthResponse> login(LoginRequest loginRequest) {
        return null;
    }

}
