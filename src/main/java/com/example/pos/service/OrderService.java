package com.example.pos.service;

import com.example.pos.dto.response.OrderDetailResponse;
import com.example.pos.dto.response.OrderResponse;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.enums.order.OrderStatus;
import io.smallrye.mutiny.Uni;

import java.time.LocalDate;
import java.util.UUID;

public interface OrderService {
    Uni<OrderResponse> openTable(UUID tableId);
    Uni<OrderResponse> confirmOrder(UUID orderId);
    Uni<OrderResponse> payOrder(UUID orderId);
    Uni<OrderResponse> cancelOrder(UUID orderId);
    Uni<OrderDetailResponse> getOrderDetail(UUID orderId);
    Uni<OrderDetailResponse> getActiveOrderByTable(UUID tableId);
    Uni<PageResponse<OrderResponse>> getOrders(
            OrderStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    );

}
