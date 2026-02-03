package com.example.pos.resource;

import com.example.pos.dto.request.GetInventoriesRequest;
import com.example.pos.dto.request.StockRequest;
import com.example.pos.dto.request.table.GetTablesRequest;
import com.example.pos.dto.response.InventoryResponse;
import com.example.pos.dto.response.common.PaginationOutput;
import com.example.pos.entity.inventory.Inventory;
import com.example.pos.entity.table.RestaurantTable;
import com.example.pos.service.impl.InventoryServiceImpl;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/inventories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InventoryResource {

    @Inject
    InventoryServiceImpl inventoryService;

    @POST
    @Path("/stock-in")
    @RolesAllowed("ADMIN")
    @Tag(
            name = "Inventory - Admin",
            description = "Admin-only inventory management APIs"
    )
    @Operation(
            summary = "Stock in product",
            description = """
            Admin only.
            Increase total and available quantity of a product in inventory.
            Used when importing goods or adjusting stock.
            """
    )
    @APIResponse(responseCode = "200", description = "Stock in successfully")
    @APIResponse(responseCode = "403", description = "Access denied")
    @APIResponse(responseCode = "404", description = "Inventory not found")
    public Uni<InventoryResponse> stockIn(StockRequest req) {
        return inventoryService.stockIn(
                req.getProductId(),
                req.getQuantity()
        );
    }

    @POST
    @Path("/stock-out")
    @RolesAllowed("ADMIN")
    @Tag(
            name = "Inventory - Admin",
            description = "Admin-only inventory management APIs"
    )
    @Operation(
            summary = "Stock out product",
            description = """
            Admin only.
            Decrease total and available quantity of a product in inventory.
            Used for manual stock adjustment or damaged goods.
            """
    )
    @APIResponse(responseCode = "200", description = "Stock out successfully")
    @APIResponse(responseCode = "403", description = "Access denied")
    @APIResponse(responseCode = "404", description = "Inventory not found")
    @APIResponse(responseCode = "409", description = "Not enough available stock")
    public Uni<InventoryResponse> stockOut(StockRequest req) {
        return inventoryService.stockOut(
                req.getProductId(),
                req.getQuantity()
        );
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Operation(
            summary = "Get inventories",
            description = """
        Retrieve a paginated list of inventory records.
        This API allows viewing all inventory items and optionally filtering by product ID.
        Accessible by both ADMIN and USER roles.
        """
    )
    @APIResponse(
            responseCode = "200",
            description = "Inventories retrieved successfully"
    )
    public Uni<PaginationOutput<Inventory>> getInventories(
            @BeanParam GetInventoriesRequest request) {
        return inventoryService.getInventories(request);
    }

}
