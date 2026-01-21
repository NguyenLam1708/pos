package com.example.pos.dto.response;

import com.example.pos.entity.table.RestaurantTable;
import com.example.pos.enums.table.TableStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RestaurantTableResponse {
    UUID id;
    String tableCode; // Tên bàn
    int capacity; // số chỗ ngồi
    TableStatus status;

    public static RestaurantTableResponse from(RestaurantTable table) {
        return RestaurantTableResponse.builder()
                .id(table.getId())
                .tableCode(table.getTableCode())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .build();
    }
}
