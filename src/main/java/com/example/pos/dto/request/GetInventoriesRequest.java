package com.example.pos.dto.request;

import com.example.pos.dto.request.common.CommonPaginateRequest;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetInventoriesRequest extends CommonPaginateRequest {

    @QueryParam("productId")
    UUID productId;
}
