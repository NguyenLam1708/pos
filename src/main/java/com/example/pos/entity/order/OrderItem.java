package com.example.pos.entity.order;

import com.example.pos.enums.order.OrderItemStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "order_items",
        indexes = {
                @Index(name = "idx_order_items_order_id", columnList = "order_id"),
                @Index(name = "idx_order_items_product_id", columnList = "product_id"),
                @Index(name = "idx_order_items_batch_no", columnList = "batch_no")
        }
)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "order_id", nullable = false)
    UUID orderId;

    @Column(name = "product_id", nullable = false)
    UUID productId;

    @Column(nullable = false)
    int quantity;

    @Column(name = "unit_price", nullable = false)
    long unitPrice;

    @Column(name = "total_price", nullable = false)
    long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderItemStatus status = OrderItemStatus.ORDERED;

    // dùng cho gọi thêm món sau confirm
    @Column(name = "batch_no")
    int batchNo = 1;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    LocalDateTime cancelledAt;

    @Column(name = "confirmed_at")
    LocalDateTime confirmedAt;

    String notes;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
