package com.example.pos.service;

import com.example.pos.dto.response.InventoryResponse;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface InventoryService {

    Uni<InventoryResponse> stockIn(UUID productId, int qty);
    Uni<InventoryResponse> stockOut(UUID productId, int qty);
}
