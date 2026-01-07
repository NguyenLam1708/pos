package com.example.pos.reponsitory;

import com.example.pos.entitiy.category.Category;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CategoryRepository implements PanacheRepositoryBase<Category, UUID> {
}
