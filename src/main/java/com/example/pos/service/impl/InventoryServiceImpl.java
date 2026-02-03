package com.example.pos.service.impl;

import com.example.pos.dto.request.GetInventoriesRequest;
import com.example.pos.dto.request.table.GetTablesRequest;
import com.example.pos.dto.response.InventoryResponse;
import com.example.pos.dto.response.common.PaginationOutput;
import com.example.pos.entity.inventory.Inventory;
import com.example.pos.entity.table.RestaurantTable;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.InventoryRepository;
import com.example.pos.service.InventoryService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class InventoryServiceImpl implements InventoryService {
    @Inject
    InventoryRepository inventoryRepository;

    @Override
    @WithTransaction
    public Uni<InventoryResponse> stockIn(UUID productId, int qty) {

        return inventoryRepository
                .lockByProductId(productId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Inventory not found")
                )
                .invoke(inv -> {
                    inv.setTotalQuantity(inv.getTotalQuantity() + qty);
                    inv.setAvailableQuantity(inv.getAvailableQuantity() + qty);
                })
                .map(InventoryResponse::from);
    }

    @Override
    @WithTransaction
    public Uni<InventoryResponse> stockOut(UUID productId, int qty) {

        return inventoryRepository
                .lockByProductId(productId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Inventory not found")
                )
                .invoke(inv -> {

                    if (inv.getAvailableQuantity() < qty) {
                        throw new BusinessException(409, "Not enough stock");
                    }

                    inv.setTotalQuantity(inv.getTotalQuantity() - qty);
                    inv.setAvailableQuantity(inv.getAvailableQuantity() - qty);
                })
                .map(InventoryResponse::from);
    }

    @Override
    @WithSession
    public Uni<PaginationOutput<Inventory>> getInventories(GetInventoriesRequest request) {
        return inventoryRepository.findInventories(request);
    }
}
