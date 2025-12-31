package com.example.pos.exception;

import com.example.pos.dto.response.ApiResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ThrowableMapper
        implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {

        // ⚠ NÊN log
        ex.printStackTrace();

        return Response.status(500)
                .entity(ApiResponse.fail("Internal server error"))
                .build();
    }
}
