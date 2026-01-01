package com.example.pos.resource;

import com.example.pos.dto.request.ChangePasswordRequest;
import com.example.pos.dto.request.CreateUserRequest;
import com.example.pos.dto.request.UpdateUserRequest;
import com.example.pos.dto.response.ApiResponse;
import com.example.pos.dto.response.UserResponse;
import com.example.pos.service.UserService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
@Path("api/v1/users")
@Tag(name = "User", description = "Operations related to users")
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @Path("/me")
    @RolesAllowed({"USER", "ADMIN"})
    @WithSession
    @Operation(summary = "Get my profile", description = "Retrieve information about the currently authenticated user.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved user info")
    public Uni<Response> getMyInfo() {
        return userService.getMyInfo()
                .map(user -> Response.ok(ApiResponse.success(user)).build());
    }

    @PUT
    @Path("/me")
    @WithSession
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Update my profile", description = "Update information of the currently authenticated user.")
    @APIResponse(responseCode = "200", description = "User profile updated successfully")
    public Uni<Response> updateMyInfo(@Valid UpdateUserRequest req) {
        return userService.updateInfo(req)
                .map(user -> Response.ok(ApiResponse.success(user)).build());
    }

    @PATCH
    @Path("/me/change-password")
    @WithSession
    @RolesAllowed({"USER", "ADMIN"})
    @Operation(summary = "Change my password", description = "Change the password of the currently authenticated user.")
    @APIResponse(responseCode = "200", description = "Password changed successfully")
    public Uni<Response> changePassword(@Valid ChangePasswordRequest req) {
        return userService.changePassword(req)
                .map(user -> Response.ok(ApiResponse.success(user)).build());
    }

    @POST
    @WithSession
    @RolesAllowed("ADMIN")
    @Operation(summary = "Create a new user", description = "Create a new user. Only ADMIN role can perform this action.")
    @APIResponse(responseCode = "201", description = "User created successfully")
    public Uni<Response> createUser(@Valid CreateUserRequest req) {
        return userService.createUser(req)
                .map(user -> Response.status(Response.Status.CREATED)
                        .entity(ApiResponse.success(user))
                        .build());
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @WithSession
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their UUID. Only ADMIN can perform this action.")
    @APIResponse(responseCode = "200", description = "User retrieved successfully")
    public Uni<Response> getUserById(
            @Parameter(description = "UUID of the user to retrieve") @PathParam("id") UUID id) {
        return userService.getUserById(id)
                .map(user -> Response.ok(ApiResponse.success(user)).build());
    }

    @PUT
    @Path("/{id}")
    @WithSession
    @RolesAllowed("ADMIN")
    @Operation(summary = "Update user by ID", description = "Update information for a specific user. Only ADMIN can perform this action.")
    @APIResponse(responseCode = "200", description = "User updated successfully")
    public Uni<Response> updateUser(
            @Parameter(description = "UUID of the user to update") @PathParam("id") UUID id,
            @Valid UpdateUserRequest req) {
        return userService.updateUser(id, req)
                .map(user -> Response.ok(ApiResponse.success(user)).build());
    }

    @PUT
    @Path("/{id}/ban")
    @WithSession
    @RolesAllowed("ADMIN")
    @Operation(summary = "Ban user", description = "Ban a specific user. Only ADMIN can perform this action.")
    @APIResponse(responseCode = "200", description = "User banned successfully")
    public Uni<Response> banUser(
            @Parameter(description = "UUID of the user to ban") @PathParam("id") UUID id) {
        return userService.banUser(id)
                .map(user -> Response.ok(ApiResponse.success(user)).build());
    }

    @PUT
    @Path("/{id}/active")
    @WithSession
    @RolesAllowed("ADMIN")
    @Operation(summary = "Activate user", description = "Activate a specific user. Only ADMIN can perform this action.")
    @APIResponse(responseCode = "200", description = "User activated successfully")
    public Uni<Response> activeUser(
            @Parameter(description = "UUID of the user to activate") @PathParam("id") UUID id) {
        return userService.activeUser(id)
                .map(user -> Response.ok(ApiResponse.success(user)).build());
    }

    @GET
    @RolesAllowed("ADMIN")
    @WithSession
    @Operation(summary = "Get paginated users", description = "Retrieve a paginated list of users. Only ADMIN can perform this action.")
    @APIResponse(responseCode = "200", description = "Users retrieved successfully")
    public Uni<Response> getUsers(
            @Parameter(description = "Page number, starting from 0") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Number of users per page") @QueryParam("size") @DefaultValue("10") int size
    ) {
        return userService.getUsers(page, size)
                .map(pageResult -> Response.ok(ApiResponse.success(pageResult)).build());
    }
}
