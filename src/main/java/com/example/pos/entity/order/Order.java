package com.example.pos.entity.order;

import com.example.pos.enums.order.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "table_id")
    UUID tableId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status = OrderStatus.OPEN;

    @Column(name = "current_batch_no", nullable = false)
    int currentBatchNo = 1;

    @Column(name = "total_amount")
    long totalAmount;

    @Column(name = "total_quantity")
    int totalQuantity;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    LocalDateTime cancelledAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
