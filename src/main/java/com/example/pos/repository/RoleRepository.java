package com.example.pos.repository;

import com.example.pos.entity.role.Role;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<Role, UUID> {
    public Uni<Role> findByCode(String code) {
        return find("code", code).firstResult();
    }
}
