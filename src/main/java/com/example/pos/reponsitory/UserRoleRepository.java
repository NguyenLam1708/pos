package com.example.pos.reponsitory;

import com.example.pos.entitiy.user.UserRole;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserRoleRepository
        implements PanacheRepositoryBase<UserRole, String> {

    public Uni<List<String>> findRoleCodesByUserId(String userId) {
        return find("""
        select r.code
        from UserRole ur, Role r
        where ur.roleId = r.id
          and ur.userId = ?1
    """, userId).project(String.class).list();
    }

}
