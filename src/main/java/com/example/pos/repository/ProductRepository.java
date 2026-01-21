package com.example.pos.repository;

import com.example.pos.entity.order.OrderItem;
import com.example.pos.entity.product.Product;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, UUID> {

    public PanacheQuery<Product> findActive() {
        return find("deletedAt IS NULL");
    }

    public Uni<Product> findActiveById(UUID id) {
        return find(
                "id = ?1 AND deletedAt IS NULL",
                id
        ).firstResult();
    }

    public Uni<Void> softDelete(Product product) {
        product.setDeletedAt(LocalDateTime.now());
        return persist(product).replaceWithVoid();
    }
}
