package com.example.pos.service;

import com.example.pos.dto.response.CategoryResponse;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.ProductResponse;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface CategoryService {
    Uni<PageResponse<CategoryResponse>> getCategories(int page, int size);
    Uni<PageResponse<ProductResponse>> getProductsByCategoryId(
            UUID categoryId,
            int page,
            int size
    );}
