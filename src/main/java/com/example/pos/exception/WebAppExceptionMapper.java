package com.example.pos.exception;

import com.example.pos.dto.response.ApiResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WebAppExceptionMapper
        implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException ex) {
        return Response.status(ex.getResponse().getStatus())
                .entity(ApiResponse.fail(ex.getMessage()))
                .build();
    }
}
