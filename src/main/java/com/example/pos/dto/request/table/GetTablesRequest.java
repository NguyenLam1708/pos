package com.example.pos.dto.request.table;

import com.example.pos.dto.request.common.CommonPaginateRequest;
import com.example.pos.enums.table.TableStatus;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetTablesRequest extends CommonPaginateRequest {

  @QueryParam("status")
  TableStatus status;
}
