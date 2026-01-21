package com.example.pos.dto.response;

import com.example.pos.entity.order.OrderItem;
import com.example.pos.entity.product.Product;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderItemResponse {

    UUID orderItemId;
    UUID productId;
    String productName;
    String imageUrl;
    int quantity;
    long unitPrice;
    long totalPrice;
    String notes;

    public static OrderItemResponse from(OrderItem orderItem, Product product) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .productId(orderItem.getProductId())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .quantity(orderItem.getQuantity())
                .notes(orderItem.getNotes())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getUnitPrice() * orderItem.getQuantity())
                .build();
    }
}
