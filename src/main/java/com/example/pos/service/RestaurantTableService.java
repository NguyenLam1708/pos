package com.example.pos.service;

import com.example.pos.dto.request.table.GetTablesRequest;
import com.example.pos.dto.response.RestaurantTableResponse;
import com.example.pos.dto.response.common.PaginationOutput;
import com.example.pos.entity.table.RestaurantTable;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface RestaurantTableService {
    Uni<PaginationOutput<RestaurantTable>> getTables(GetTablesRequest request);
    Uni<RestaurantTableResponse> getTable(UUID tableId);

}
