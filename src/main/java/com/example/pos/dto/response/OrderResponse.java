package com.example.pos.dto.response;

import com.example.pos.entity.order.Order;
import com.example.pos.enums.order.OrderStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    UUID orderId;
    UUID tableId;
    OrderStatus status;
    int currentBatchNo = 1;
    long totalAmount;
    int totalQuantity;
    LocalDateTime createdAt;
    LocalDateTime confirmedAt;
    LocalDateTime cancelledAt;    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .tableId(order.getTableId())
                .status(order.getStatus())
                .currentBatchNo(order.getCurrentBatchNo())
                .totalAmount(order.getTotalAmount())
                .totalQuantity(order.getTotalQuantity())
                .createdAt(order.getCreatedAt())
                .confirmedAt(order.getConfirmedAt())
                .cancelledAt(order.getCancelledAt())
                .build();
    }
}
