package com.example.pos.service.impl;

import com.example.pos.dto.response.*;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.RestaurantTableRepository;
import com.example.pos.service.RestaurantTableService;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RestaurantTableServiceImpl implements RestaurantTableService {

    @Inject
    RestaurantTableRepository restaurantTableRepository;



    @Override
    public Uni<PageResponse<RestaurantTableResponse>> getTables(int page, int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        var query = restaurantTableRepository.findAllSorted();

        Uni<Long> countUni = query.count();

        Uni<List<RestaurantTableResponse>> itemsUni =
                query.page(Page.of(safePage, safeSize))
                        .list()
                        .map(list -> list.stream()
                                .map(RestaurantTableResponse::from)
                                .toList());

        return Uni.combine().all().unis(itemsUni, countUni)
                .asTuple()
                .map(tuple -> {
                    long totalItems = tuple.getItem2();
                    int totalPages = (int) Math.ceil((double) totalItems / safeSize);

                    return PageResponse.<RestaurantTableResponse>builder()
                            .items(tuple.getItem1())
                            .page(safePage)
                            .size(safeSize)
                            .totalItems(totalItems)
                            .totalPages(totalPages)
                            .hasNext(safePage < totalPages - 1)
                            .hasPrevious(safePage > 0)
                            .build();
                });
    }

    @Override
    public Uni<RestaurantTableResponse> getTable(UUID tableId) {
        return restaurantTableRepository.findById(tableId)
                .onItem().ifNull()
                .failWith(()-> new BusinessException(404,"Table not found"))
                .map(RestaurantTableResponse::from);
    }

}
