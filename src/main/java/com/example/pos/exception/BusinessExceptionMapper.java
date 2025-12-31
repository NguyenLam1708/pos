package com.example.pos.exception;

import com.example.pos.dto.response.ApiResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper
        implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException ex) {
        return Response.status(ex.getStatus())
                .entity(ApiResponse.fail(ex.getMessage()))
                .build();
    }
}
