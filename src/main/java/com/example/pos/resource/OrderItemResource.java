package com.example.pos.resource;

import com.example.pos.dto.request.AddOrderItemRequest;
import com.example.pos.dto.response.ApiResponse;
import com.example.pos.dto.response.OrderResponse;
import com.example.pos.service.OrderItemService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@ApplicationScoped
@Path("/api/v1/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Order Item", description = "Operations related to order items")
public class OrderItemResource {

    @Inject
    OrderItemService orderItemService;

    @POST
    @Path("/{orderId}/items")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Add item to order",
            description = "Add a product item to an active order. " +
                    "If the product already exists in the order, its quantity will be increased."
    )
    @APIResponse(responseCode = "200", description = "Item added to order successfully")
    @APIResponse(responseCode = "404", description = "Order or product not found")
    @APIResponse(responseCode = "400", description = "Order is not in OPEN state")
    public Uni<Response> addItem(
            @Parameter(description = "Order ID", required = true)
            @PathParam("orderId") UUID orderId,
            AddOrderItemRequest request
    ) {
        return orderItemService.addItem(orderId, request)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }

    @DELETE
    @Path("/{orderId}/items/{orderItemId}")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Cancel order item",
            description = """
            Cancel a specific item in an order.

            Business rules:
            - Order must be in OPEN status
            - Item must not be already cancelled
            - Inventory will be restored
            - Inventory reservation will be released

            This action does NOT delete the item physically.
            The item status will be set to CANCELLED.
            """
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Order item cancelled successfully"),
            @APIResponse(responseCode = "400", description = "Order is not open or item already cancelled"),
            @APIResponse(responseCode = "404", description = "Order or item not found"),
            @APIResponse(responseCode = "403", description = "Access denied"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Uni<Response> cancelOrderItem(
            @Parameter(
                    description = "Order ID",
                    required = true
            )
            @PathParam("orderId") UUID orderId,

            @Parameter(
                    description = "Order item ID",
                    required = true
            )
            @PathParam("orderItemId") UUID orderItemId
    ) {
        return orderItemService
                .cancelItem(orderId, orderItemId)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }
}
