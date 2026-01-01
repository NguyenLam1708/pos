package com.example.pos.resource;

import com.example.pos.dto.request.LoginRequest;
import com.example.pos.dto.response.ApiResponse;
import com.example.pos.service.AuthService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    @WithSession
    @Operation(
            summary = "User login",
            description = "Authenticate a user using email and password. Returns JWT token on success."
    )
    @APIResponse(responseCode = "200", description = "Login successful, returns JWT token and user info")
    @APIResponse(responseCode = "400", description = "Invalid credentials or request")
    public Uni<Response> login(@Valid LoginRequest req) {
        log.info("Login request received for email: {}", req.getEmail());

        return authService.login(req)
                .map(data -> Response.ok(ApiResponse.success(data)).build());
    }
}
