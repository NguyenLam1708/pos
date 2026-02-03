package com.example.pos.repository;

import com.example.pos.entity.order.OrderItem;
import com.example.pos.enums.order.OrderItemStatus;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderItemRepository
        implements PanacheRepositoryBase<OrderItem, UUID> {

    /**
     * Lấy tất cả item của order (KHÔNG nên dùng cho order detail)
     * Chỉ dùng khi thật sự cần raw data
     */
    public Uni<List<OrderItem>> findByOrderId(UUID orderId) {
        return find("orderId", orderId).list();
    }

    /**
     * Tìm item đang ORDERED để gộp quantity
     */
    public Uni<OrderItem> findByOrderProductBatchAndNotes(
            UUID orderId,
            UUID productId,
            int batchNo,
            String notes
    ) {
        if (notes == null || notes.isBlank()) {
            return find(
                    """
                    orderId = :orderId
                    and productId = :productId
                    and batchNo = :batchNo
                    and status = :status
                    and notes is null
                    """,
                    Parameters.with("orderId", orderId)
                            .and("productId", productId)
                            .and("batchNo", batchNo)
                            .and("status", OrderItemStatus.ORDERED)
            ).firstResult();
        }

        return find(
                """
                orderId = :orderId
                and productId = :productId
                and batchNo = :batchNo
                and status = :status
                and notes = :notes
                """,
                Parameters.with("orderId", orderId)
                        .and("productId", productId)
                        .and("batchNo", batchNo)
                        .and("status", OrderItemStatus.ORDERED)
                        .and("notes", notes)
        ).firstResult();
    }

    /**
     * ✅ QUERY CHUẨN CHO ORDER DETAIL
     * - JOIN Product
     * - KHÔNG lấy item CANCELLED
     * - 1 query duy nhất (Hibernate Reactive safe)
     */
    public Uni<List<Object[]>> findActiveItemsWithProduct(UUID orderId) {
        return find(
                """
                select oi, p
                from OrderItem oi
                join Product p on p.id = oi.productId
                where oi.orderId = :orderId
                  and oi.status <> :cancelled
                order by oi.createdAt
                """,
                Parameters.with("orderId", orderId)
                        .and("cancelled", OrderItemStatus.CANCELLED)
        ).project(Object[].class).list();
    }

    /**
     * Tìm 1 item cụ thể trong order
     */
    public Uni<OrderItem> findByOrderAndId(UUID orderId, UUID orderItemId) {
        return find(
                "orderId = :orderId and id = :id",
                Parameters.with("orderId", orderId)
                        .and("id", orderItemId)
        ).firstResult();
    }

    public Uni<List<OrderItem>> findOrderedItemsByOrderAndBatch(
            UUID orderId,
            int batchNo
    ) {
        return find(
                "orderId = ?1 and batchNo = ?2 and status = ?3",
                orderId,
                batchNo,
                OrderItemStatus.ORDERED
        ).list();
    }

    public Uni<Boolean> existsConfirmedItemInBatch(UUID orderId, int batchNo) {
        return find(
                "orderId = ?1 and batchNo = ?2 and status = ?3",
                orderId,
                batchNo,
                OrderItemStatus.CONFIRMED
        ).count()
                .map(count -> count > 0);
    }

    public Uni<Boolean> existsConfirmedItem(UUID orderId) {
        return find(
                "orderId = ?1 and status = ?2",
                orderId,
                OrderItemStatus.CONFIRMED
        ).firstResult()
                .map(item -> item != null);
    }

}
