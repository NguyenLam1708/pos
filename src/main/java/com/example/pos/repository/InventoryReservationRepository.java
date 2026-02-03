package com.example.pos.repository;

import com.example.pos.entity.inventory.InventoryReservation;
import com.example.pos.enums.inventory.ReservationStatus;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class InventoryReservationRepository implements PanacheRepositoryBase<InventoryReservation, UUID> {
    public Uni<List<InventoryReservation>> findByOrderId(UUID orderId) {
        return find("orderId", orderId).list();
    }

    public Uni<InventoryReservation> findActiveByOrderProductAndBatch(
            UUID orderId,
            UUID productId,
            int batchNo
    ) {
        return find(
                "orderId = ?1 and productId = ?2 and batchNo = ?3 and status = ?4",
                orderId,
                productId,
                batchNo,
                ReservationStatus.RESERVED
        ).firstResult();
    }

    public Uni<List<InventoryReservation>> findActiveByOrderIdAndBatch(
            UUID orderId,
            int batchNo
    ) {
        return find(
                "orderId = ?1 and batchNo = ?2 and status = ?3",
                orderId,
                batchNo,
                ReservationStatus.RESERVED
        ).list();
    }

    public Uni<Long> sumActiveReservedByProduct(@NotNull UUID productId) {

        return getSession()
                .flatMap(session ->
                        session.createQuery(
                                        """
                                        select coalesce(sum(r.quantity), 0)
                                        from InventoryReservation r
                                        where r.productId = :productId
                                          and r.status = :status
                                          and r.expiresAt > :now
                                        """,
                                        Long.class
                                )
                                .setParameter("productId", productId)
                                .setParameter("status", ReservationStatus.RESERVED)
                                .setParameter("now", LocalDateTime.now())
                                .getSingleResult()
                );
    }

}
