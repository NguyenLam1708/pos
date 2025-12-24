package com.example.pos.security;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.security.Principal;

@AllArgsConstructor
@Getter
@Setter
public class AuthenticatedUser implements Principal {
    private final String userId;
    private final String email;
    private final String phoneNumber;

    @Override
    public String getName() {
        return email;
    }

}