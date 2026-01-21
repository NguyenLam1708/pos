package com.example.pos.repository;

import com.example.pos.entity.user.User;
import com.example.pos.enums.user.UserStatus;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID> {
    public Uni<Boolean> existsByEmail(String email) {
        return count("email",email)
                .map(count -> count > 0);
    }
    public Uni<User> findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
