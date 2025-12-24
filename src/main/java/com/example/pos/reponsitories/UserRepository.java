package com.example.pos.reponsitories;

import com.example.pos.entities.user.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, String> {

    public Uni<User> findByUsername(String username) {
        return find("username", username).firstResult();
    }
}
