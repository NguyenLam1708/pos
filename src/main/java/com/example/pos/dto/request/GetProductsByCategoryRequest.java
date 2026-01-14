package com.example.pos.dto.request;

import jakarta.ws.rs.PathParam;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.util.UUID;

@Data
public class GetProductsByCategoryRequest extends PaginationRequest{
    @Parameter(description = "Category ID")
    @PathParam("id")
    UUID categoryId;
}
