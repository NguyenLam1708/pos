package com.example.pos.dto.request.common;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@Schema(name = "CommonPaginateRequest", description = "Common pagination request parameters")
public class CommonPaginateRequest {

  @QueryParam("page")
  @DefaultValue("1")
  @Schema(description = "Page number (0-indexed)", examples = "0", defaultValue = "0")
  private Integer page;

  @QueryParam("size")
  @DefaultValue("10")
  @Schema(description = "Number of items per page", examples = "10", defaultValue = "10")
  private Integer size;

  public PaginateInput getPaginateInput() {
    PaginateInput input = new PaginateInput();
    if (page != null) {
      page = page > 1 ? page : 1;
    } else {
      page = 1;
    }
    input.setPage(page);
    if (size != null) {
      size = size > 0 ? size > 100 ? 100 : size : 10;
    } else {
      size = 10;
    }
    input.setSize(size);
    return input;
  }

}
