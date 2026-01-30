package com.example.pos.resource;

import com.example.pos.dto.response.ApiResponse;
import com.example.pos.dto.response.OrderResponse;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.enums.order.OrderStatus;
import com.example.pos.service.OrderService;
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

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
@Path("/api/v1/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Order", description = "Operations related to orders and table ordering")
public class OrderResource {

    @Inject
    OrderService orderService;

    @POST
    @Path("/tables/{tableId}/open")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Open table (create new order)",
            description = "Open a table and create a new active order for the given table"
    )
    @APIResponse(responseCode = "200", description = "Table opened and order created")
    @APIResponse(responseCode = "404", description = "Table not found")
    @APIResponse(responseCode = "400", description = "Table already has an active order")
    public Uni<Response> openTable(
            @Parameter(description = "Restaurant table ID", required = true)
            @PathParam("tableId") UUID tableId
    ) {
        return orderService.openTable(tableId)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }

    @POST
    @Path("/{orderId}/confirm")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Confirm order",
            description = "Confirm an order so it can no longer be modified"
    )
    @APIResponse(responseCode = "200", description = "Order confirmed successfully")
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "400", description = "Invalid order state")
    public Uni<Response> confirmOrder(
            @Parameter(description = "Order ID", required = true)
            @PathParam("orderId") UUID orderId
    ) {
        return orderService.confirmOrder(orderId)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }

    @POST
    @Path("/{orderId}/pay")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Pay order",
            description = "Pay an order and close it"
    )
    @APIResponse(responseCode = "200", description = "Order paid successfully")
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "400", description = "Order cannot be paid")
    public Uni<Response> payOrder(
            @Parameter(description = "Order ID", required = true)
            @PathParam("orderId") UUID orderId
    ) {
        return orderService.payOrder(orderId)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }

    @POST
    @Path("/{orderId}/cancel")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Cancel order",
            description = """
        Cancel an active order.

        Allowed roles: ADMIN, USER.

        Business rules:
        - Cannot cancel an order that is already PAID or CANCELLED.
        - All ORDERED items in the current batch will be cancelled.
        - Inventory reservations of the current batch will be released.
        - Available inventory quantity will be restored.
        - Order status will be set to CANCELLED.
        """
    )
    @APIResponse(responseCode = "200", description = "Order cancelled successfully")
    @APIResponse(responseCode = "400", description = "Order cannot be cancelled")
    @APIResponse(responseCode = "404", description = "Order not found")
    @APIResponse(responseCode = "403", description = "Access denied")
    public Uni<Response> cancelOrder(
            @Parameter(description = "Order ID", required = true)
            @PathParam("orderId") UUID orderId
    ) {
        return orderService.cancelOrder(orderId)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }

    @GET
    @Path("/{orderId}")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Get order detail",
            description = "Retrieve full order detail including items"
    )
    @APIResponse(responseCode = "200", description = "Order detail retrieved successfully")
    @APIResponse(responseCode = "404", description = "Order not found")
    public Uni<Response> getOrderDetail(
            @Parameter(description = "Order ID", required = true)
            @PathParam("orderId") UUID orderId
    ) {
        return orderService.getOrderDetail(orderId)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }

    @GET
    @Path("/tables/{tableId}/active")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Get active order by table",
            description = "Retrieve the current active order for a table"
    )
    @APIResponse(responseCode = "200", description = "Active order retrieved successfully")
    @APIResponse(responseCode = "404", description = "No active order found for table")
    public Uni<Response> getActiveOrderByTable(
            @Parameter(description = "Restaurant table ID", required = true)
            @PathParam("tableId") UUID tableId
    ) {
        return orderService.getActiveOrderByTable(tableId)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }

    @GET
    @WithSession
    @RolesAllowed("ADMIN")
    @Tag(name = "Order - Admin", description = "Admin-only order management APIs")
    @Operation(
            summary = "Get orders with pagination and filters",
            description = """
        Admin API.
        Retrieve all orders with pagination.
        Supports filtering by status and created date range.
        """
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @APIResponse(responseCode = "400", description = "Invalid query parameters"),
            @APIResponse(responseCode = "403", description = "Access denied"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Uni<PageResponse<OrderResponse>> getOrders(

            @Parameter(description = "Order status filter")
            @QueryParam("status") OrderStatus status,

            @Parameter(description = "Filter orders created from this date (yyyy-MM-dd)")
            @QueryParam("fromDate") LocalDate fromDate,

            @Parameter(description = "Filter orders created until this date (yyyy-MM-dd)")
            @QueryParam("toDate") LocalDate toDate,

            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return orderService.getOrders(
                status,
                fromDate,
                toDate,
                page,
                size
        );
    }

}
