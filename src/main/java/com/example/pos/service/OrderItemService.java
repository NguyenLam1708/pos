package com.example.pos.service;

import com.example.pos.dto.request.AddOrderItemRequest;
import com.example.pos.dto.response.OrderDetailResponse;
import com.example.pos.dto.response.OrderResponse;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface OrderItemService {
    Uni<OrderDetailResponse> addItem(UUID orderId, AddOrderItemRequest req);
    Uni<OrderDetailResponse> cancelItem(UUID orderId, UUID orderItemId);
}
