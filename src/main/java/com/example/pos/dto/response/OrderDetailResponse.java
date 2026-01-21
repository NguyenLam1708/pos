package com.example.pos.dto.response;

import com.example.pos.entity.order.Order;
import com.example.pos.entity.order.OrderItem;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderDetailResponse extends OrderResponse {

    private UUID orderItemId;
    private List<OrderItemResponse> items;
    private int totalItems;
    private int totalQuantity;
    private long totalAmount;

    public static OrderDetailResponse from(Order order) {
        OrderDetailResponse response = new OrderDetailResponse();

        response.setOrderId(order.getId());
        response.setTableId(order.getTableId());
        response.setStatus(order.getStatus());

        return response;
    }
}
