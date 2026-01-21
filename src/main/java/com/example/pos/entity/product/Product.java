package com.example.pos.entity.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID )
    UUID id;

    String name;

    @Column(name = "category_id")
    UUID categoryId;

    long price;

    @Column(name="image_url")
    String imageUrl;

    @Column(name="thumbnail_url")
    String thumbnailUrl;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
