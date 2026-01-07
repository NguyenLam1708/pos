package com.example.pos.reponsitory;

import com.example.pos.entitiy.product.Product;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, UUID> {
}
