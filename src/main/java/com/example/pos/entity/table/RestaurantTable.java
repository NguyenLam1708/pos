package com.example.pos.entity.table;

import com.example.pos.enums.table.TableStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "restaurant_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, unique = true,name = "table_code")
    String tableCode; // Tên bàn

    int capacity; // số chỗ ngồi

    @Enumerated(EnumType.STRING)
    TableStatus status;
}
