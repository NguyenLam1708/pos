package com.example.pos.repository;

import com.example.pos.entity.inventory.Inventory;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.persistence.LockModeType;
import java.util.UUID;

@ApplicationScoped
public class InventoryRepository implements PanacheRepositoryBase<Inventory, UUID> {

    public Uni<Inventory> lockByProductId(UUID productId) {
        return find("productId", productId)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }
}
