package com.example.pos.resource;

import com.example.pos.dto.response.ApiResponse;
import com.example.pos.service.RestaurantTableService;
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
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@ApplicationScoped
@Path("/api/v1/tables")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(
        name = "Restaurant Table",
        description = "APIs for managing and viewing restaurant tables"
)
public class RestaurantTableResource {

    @Inject
    RestaurantTableService tableService;

    @GET
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Get restaurant tables",
            description = "Retrieve a paginated list of restaurant tables. " +
                    "This API is accessible by both ADMIN and USER roles."
    )
    @APIResponse(
            responseCode = "200",
            description = "Tables retrieved successfully"
    )
    public Uni<Response> getTables(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return tableService
                .getTables(page, size)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }

    @GET
    @Path("/{id}")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Get table by ID",
            description = "Retrieve detailed information of a restaurant table by its unique ID."
    )
    @APIResponse(
            responseCode = "200",
            description = "Table retrieved successfully"
    )
    @APIResponse(
            responseCode = "404",
            description = "Table not found"
    )
    public Uni<Response> getTableById(
            @Parameter(
                    description = "Restaurant table ID",
                    required = true
            )
            @PathParam("id") UUID tableId
    ) {
        return tableService
                .getTable(tableId)
                .map(result ->
                        Response.ok(ApiResponse.success(result)).build()
                );
    }
}
