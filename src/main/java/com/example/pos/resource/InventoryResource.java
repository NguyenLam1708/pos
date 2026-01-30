package com.example.pos.resource;

import com.example.pos.dto.request.StockRequest;
import com.example.pos.dto.response.InventoryResponse;
import com.example.pos.service.impl.InventoryServiceImpl;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/inventories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

    @Inject
    InventoryServiceImpl inventoryService;

    @POST
    @Path("/stock-in")
    public Uni<InventoryResponse> stockIn(StockRequest req) {
        return inventoryService.stockIn(req.getProductId(), req.getQuantity());
    }

    @POST
    @Path("/stock-out")
    public Uni<InventoryResponse> stockOut(StockRequest req) {
        return inventoryService.stockOut(req.getProductId(), req.getQuantity());
    }
}
