package com.example.pos.service;

import com.example.pos.dto.request.GetInventoriesRequest;
import com.example.pos.dto.request.table.GetTablesRequest;
import com.example.pos.dto.response.InventoryResponse;
import com.example.pos.dto.response.common.PaginationOutput;
import com.example.pos.entity.inventory.Inventory;
import com.example.pos.entity.table.RestaurantTable;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface InventoryService {

    Uni<InventoryResponse> stockIn(UUID productId, int qty);
    Uni<InventoryResponse> stockOut(UUID productId, int qty);

    Uni<PaginationOutput<Inventory>> getInventories(GetInventoriesRequest request);

}
