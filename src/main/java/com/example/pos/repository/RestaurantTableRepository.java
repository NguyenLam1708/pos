package com.example.pos.repository;

import com.example.pos.entity.table.RestaurantTable;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;


import java.util.UUID;

@ApplicationScoped
public class RestaurantTableRepository
        implements PanacheRepositoryBase<RestaurantTable, UUID> {

    public Uni<RestaurantTable> lockById(UUID tableId) {
        return find("id", tableId)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }
}

