package com.example.pos.dto.request;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Data
@NoArgsConstructor
public abstract class PaginationRequest {

    @Parameter(description = "Page number, starting from 0")
    @QueryParam("page")
    @DefaultValue("0")
    private int page;

    @Parameter(description = "Number of products per page")
    @QueryParam("size")
    @DefaultValue("10")
    private int size;

}
