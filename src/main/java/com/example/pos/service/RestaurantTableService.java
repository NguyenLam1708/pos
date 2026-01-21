package com.example.pos.service;

import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.RestaurantTableResponse;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface RestaurantTableService {
    Uni<PageResponse<RestaurantTableResponse>> getTables(int page, int size);
    Uni<RestaurantTableResponse> getTable(UUID tableId);

}
