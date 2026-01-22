package com.example.pos.resource;

import com.example.pos.dto.request.CreateProductRequest;
import com.example.pos.dto.request.ImageUploadForm;
import com.example.pos.dto.request.UpdateProductRequest;
import com.example.pos.dto.response.ApiResponse;
import com.example.pos.service.ProductService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.NonBlocking;
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
@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Tag(
        name = "Product",
        description = "APIs for managing products, including images"
)
public class  ProductResource {

    @Inject
    ProductService productService;

    @GET
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Get products (paginated)",
            description = "Retrieve a paginated list of active products. Deleted products are excluded."
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Products retrieved successfully"),
            @APIResponse(responseCode = "403", description = "Access denied"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Uni<Response> getProducts(
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size
    ) {
        return productService
                .getProducts(page, size)
                .map(r -> Response.ok(ApiResponse.success(r)).build());
    }

    @GET
    @Path("/{id}")
    @WithSession
    @RolesAllowed({ "ADMIN", "USER" })
    @Operation(
            summary = "Get product by ID",
            description = "Retrieve product details by product ID"
    )
    @APIResponse(responseCode = "200", description = "Product found")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Uni<Response> getProductById(
            @Parameter(
                    description = "Product ID",
                    required = true
            )
            @PathParam("id") UUID id
    ) {
        return productService
                .getProductById(id)
                .map(r -> Response.ok(ApiResponse.success(r)).build());
    }

    @POST
    @WithSession
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    @Tag(name = "Product - Admin", description = "Admin-only product management APIs")
    @Operation(
            summary = "Create product",
            description = """
            Admin only.
            Create a new product without image.
            Product image must be uploaded separately.
            """
    )
    @APIResponse(responseCode = "201", description = "Product created")
    @APIResponse(responseCode = "403", description = "Access denied")

    public Uni<Response> createProduct(
            @Parameter(
                    description = "Product creation payload",
                    required = true
            )
            CreateProductRequest req
    ) {
        return productService
                .createProduct(req)
                .map(r -> Response
                        .status(Response.Status.CREATED)
                        .entity(ApiResponse.success(r))
                        .build());
    }

    @PUT
    @Path("/{id}")
    @WithSession
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    @Tag(name = "Product - Admin")
    @Operation(
            summary = "Update product",
            description = """
            Admin only.
            Update product basic information such as name, price, or category.
            """
    )
    @APIResponse(responseCode = "200", description = "Product updated")
    @APIResponse(responseCode = "404", description = "Product not found")
    @APIResponse(responseCode = "403", description = "Access denied")
    public Uni<Response> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathParam("id") UUID id,
            UpdateProductRequest req
    ) {
        return productService
                .updateProduct(id, req)
                .map(r -> Response.ok(ApiResponse.success(r)).build());
    }

    @DELETE
    @Path("/{id}")
    @WithSession
    @RolesAllowed("ADMIN")
    @Tag(name = "Product - Admin")
    @Operation(
            summary = "Delete product",
            description = """
            Admin only.
            Soft delete product.
            The product will no longer be visible to users.
            """
    )
    @APIResponse(responseCode = "204", description = "Product deleted")
    @APIResponse(responseCode = "403", description = "Access denied")
    public Uni<Response> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathParam("id") UUID id
    ) {
        return productService
                .deleteProduct(id)
                .replaceWith(Response.noContent().build());
    }

    @POST
    @Path("/{id}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Blocking
    @RolesAllowed("ADMIN")
    @Tag(name = "Product - Admin")
    @Operation(
            summary = "Upload product image",
            description = """
        Admin only.
        Upload or replace product image.
        Image will be converted to WebP and a thumbnail will be generated.
        """
    )
    @APIResponse(responseCode = "200", description = "Image uploaded successfully")
    @APIResponse(responseCode = "404", description = "Product not found")
    @APIResponse(responseCode = "403", description = "Access denied")
    public Uni<Response> uploadImage(
            @Parameter(description = "Product ID", required = true)
            @PathParam("id") UUID id,
            @BeanParam ImageUploadForm form
    ) {
        return productService
                .uploadProductImage(id, form)
                .map(r -> Response.ok(ApiResponse.success(r)).build());
    }

    @DELETE
    @Path("/{id}/image")
    @NonBlocking
    @RolesAllowed("ADMIN")
    @Tag(name = "Product - Admin")
    @Operation(
            summary = "Delete product image",
            description = """
        Admin only.
        Remove product image and thumbnail.
        """
    )
    @APIResponse(responseCode = "204", description = "Image deleted")
    @APIResponse(responseCode = "403", description = "Access denied")
    public Uni<Response> deleteImage(
            @Parameter(description = "Product ID", required = true)
            @PathParam("id") UUID id
    ) {
        return productService
                .deleteProductImage(id)
                .replaceWith(Response.noContent().build());
    }

}
