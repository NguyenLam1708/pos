package com.example.pos.resource;

import com.example.pos.dto.request.GetCategoriesRequest;
import com.example.pos.dto.request.GetProductsByCategoryRequest;
import com.example.pos.dto.response.ApiResponse;
import com.example.pos.service.CategoryService;
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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("api/v1/category")
@Tag(name = "Category", description = "Operations related to category")
public class CategoryResource {
    @Inject
    CategoryService categoryService;

    @GET
    @RolesAllowed("ADMIN")
    @WithSession
    @Operation(summary = "Get paginated categories", description = "Retrieve a paginated list of categories. Only ADMIN can perform this action.")
    @APIResponse(responseCode = "200", description = "Categories retrieved successfully")
    public Uni<Response> getCategories(
            @BeanParam GetCategoriesRequest request
    ) {
        return categoryService
                .getCategories(request.getPage(), request.getSize())
                .map(pageResult -> Response.ok(ApiResponse.success(pageResult)).build());
    }

    @GET
    @Path("/{id}/products")
    @RolesAllowed({ "ADMIN", "USER" })
    @WithSession
    @Operation(
            summary = "Get products by category",
            description = "Retrieve a paginated list of products by category ID."
    )
    @APIResponse(responseCode = "200", description = "Products retrieved successfully")
    @APIResponse(responseCode = "404", description = "Category not found")
    public Uni<Response> getProductsByCategory(
            @BeanParam GetProductsByCategoryRequest request
    ) {
        return categoryService
                .getProductsByCategoryId(request.getCategoryId(), request.getPage(), request.getSize())
                .map(result -> Response.ok(ApiResponse.success(result)).build());
    }
}
