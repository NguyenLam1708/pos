package com.example.pos.service;

import com.example.pos.dto.request.CreateProductRequest;
import com.example.pos.dto.request.ImageUploadForm;
import com.example.pos.dto.request.UpdateProductRequest;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.ProductResponse;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface ProductService {

    // Admin
    Uni<ProductResponse> createProduct(CreateProductRequest req);
    Uni<ProductResponse> updateProduct(UUID productId, UpdateProductRequest req);
    Uni<Void> deleteProduct(UUID productId); // soft delete

    Uni<ProductResponse> uploadProductImage(UUID productId, ImageUploadForm form);
    Uni<Void> deleteProductImage(UUID productId);

    // User / Admin
    Uni<ProductResponse> getProductById(UUID id);
    Uni<PageResponse<ProductResponse>> getProducts(int page, int size);
}
