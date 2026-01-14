package com.example.pos.resource;

import com.example.pos.dto.request.GetProductRequest;
import com.example.pos.dto.response.ApiResponse;
import com.example.pos.service.ProductService;
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
@Path("/api/v1/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product", description = "Operations related to products")
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Get paginated products",
            description = "Retrieve a paginated list of products"
    )
    @APIResponse(responseCode = "200", description = "Products retrieved successfully")
    public Uni<Response> getProducts(
            @BeanParam GetProductRequest request
    ) {
        return productService
                .getProducts(request.getPage(), request.getSize())
                .map(result -> Response.ok(ApiResponse.success(result)).build());
    }

    @GET
    @Path("/{id}")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Get product by ID",
            description = "Retrieve a product by its ID"
    )
    @APIResponse(responseCode = "200", description = "Product retrieved successfully")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Uni<Response> getProductById(
            @Parameter(description = "Product ID")
            @PathParam("id") UUID id
    ) {
        return productService.getProductById(id)
                .map(result -> Response.ok(ApiResponse.success(result)).build());
    }
}
