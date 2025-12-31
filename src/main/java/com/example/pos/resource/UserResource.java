package com.example.pos.resource;

import com.example.pos.dto.request.CreateUserRequest;
import com.example.pos.dto.response.ApiResponse;
import com.example.pos.service.UserService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("api/v1/users")
public class UserResource {
    @Inject
    UserService userService;

    @POST
    @WithSession
    //@RolesAllowed("ADMIN")
    public Uni<Response> createUser(CreateUserRequest req) {

        return userService.createUser(req)
                .map(user ->
                        Response.status(Response.Status.CREATED)
                                .entity(ApiResponse.success(user))
                                .build()
                );
    }

}
