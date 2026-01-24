package com.example.pos.dto.response;

import com.example.pos.entity.product.Product;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    UUID productId;
    String name;
    UUID categoryId;
    long price;
    String imageUrl;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }
}
