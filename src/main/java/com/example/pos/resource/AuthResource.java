package com.example.pos.resource;

import com.example.pos.dto.request.LoginRequest;
import com.example.pos.dto.response.ApiResponse;
import com.example.pos.service.AuthService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Path("/api/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Slf4j
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    @WithSession
    public Uni<Response> login(LoginRequest req) {
        log.info("Login request received for email: {}", req.getEmail());

        return authService.login(req)
                .map(data ->
                        Response.ok(ApiResponse.success(data)).build()
                );
    }
}
