package com.example.pos.dto.request.common;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(name = "PaginateInput", description = "Pagination parameters")
public class PaginateInput {

  @QueryParam("page")
  @Schema(description = "Page number (0-indexed)", defaultValue = "0", examples = "0")
  private Integer page;

  @QueryParam("size")
  @Schema(description = "Number of items per page", defaultValue = "10", examples = "10")
  private Integer size;

}
