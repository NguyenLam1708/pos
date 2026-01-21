package com.example.pos.entity.inventory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "product_id",nullable = false)
    UUID productId;

    @Column(name = "total_quantity")
    int totalQuantity; // Tổng tồn

    @Column(name = "available_quantity")
    int availableQuantity; // có thể bán
}
