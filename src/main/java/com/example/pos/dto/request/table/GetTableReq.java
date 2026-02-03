package com.example.pos.dto.request.table;

import com.example.pos.dto.request.common.CommonPaginateRequest;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetTableReq extends CommonPaginateRequest {

  String id;

}
