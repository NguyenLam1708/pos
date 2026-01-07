package com.example.pos.service;

import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.ProductResponse;
import com.example.pos.dto.response.UserResponse;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface ProductService {
    Uni<ProductResponse> getProductById(UUID id);

    Uni<PageResponse<ProductResponse>> getProducts(int page, int size);
}
