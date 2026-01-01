package com.example.pos.entitiy.inventory;

import com.example.pos.enums.inventory.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "order_id", nullable = false)
    String orderId;
    @Column(name = "product_id", nullable = false)
    String productId;

    int quantity;

    @Enumerated(EnumType.STRING)
    ReservationStatus status;

    LocalDateTime expiresAt;  // giữ chỗ trong X phút
}
