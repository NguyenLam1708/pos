package com.example.pos.reponsitories;

import com.example.pos.entities.role.Role;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<Role, String> {

    public Uni<Set<String>> findRolesByUserId(String userId) {
        return find("""
            select r.name from Role r
            join r.users u
            where u.id = ?1
        """, userId)
                .project(String.class)
                .list()
                .map(Set::copyOf);
    }}
