package com.example.pos.repository;

import com.example.pos.entity.inventory.InventoryReservation;
import com.example.pos.enums.inventory.ReservationStatus;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class InventoryReservationRepository implements PanacheRepositoryBase<InventoryReservation, UUID> {
    public Uni<List<InventoryReservation>> findByOrderId(UUID orderId) {
        return find("orderId", orderId).list();
    }

    public Uni<InventoryReservation> findActiveByOrderAndProduct(
            UUID orderId,
            UUID productId
    ) {
        return find(
                "orderId = ?1 and productId = ?2 and status = ?3",
                orderId,
                productId,
                ReservationStatus.RESERVED
        ).firstResult();
    }

}
