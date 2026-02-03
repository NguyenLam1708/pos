package com.example.pos.service.impl;

import com.example.pos.dto.request.table.GetTablesRequest;
import com.example.pos.dto.response.*;
import com.example.pos.dto.response.common.PaginationOutput;
import com.example.pos.entity.table.RestaurantTable;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.RestaurantTableRepository;
import com.example.pos.service.RestaurantTableService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class RestaurantTableServiceImpl implements RestaurantTableService {

    @Inject
    RestaurantTableRepository restaurantTableRepository;

    @Override
    @WithSession
    public Uni<PaginationOutput<RestaurantTable>> getTables(GetTablesRequest request) {
        return restaurantTableRepository.findByStatus(request);
    }

    @Override
    public Uni<RestaurantTableResponse> getTable(UUID tableId) {
        return restaurantTableRepository.findById(tableId)
                .onItem().ifNull()
                .failWith(()-> new BusinessException(404,"Table not found"))
                .map(RestaurantTableResponse::from);
    }

}
