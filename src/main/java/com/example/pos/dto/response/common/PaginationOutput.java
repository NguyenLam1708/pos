package com.example.pos.dto.response.common;

import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(name = "PaginationOutput", description = "Generic pagination output structure")
public class PaginationOutput<T> {

  @Schema(description = "List of items for the current page")
  private List<T> data;

  @Schema(description = "Total number of items across all pages", examples = "100")
  private long total;

  @Schema(description = "Current page number (0-indexed)", examples = "0")
  private int page;

  @Schema(description = "Number of items per page", examples = "10")
  private int size;

  public <R> PaginationOutput<R> convertData(Function<? super T, R> mapper) {
    var out = new PaginationOutput<R>();
    out.setPage(this.page)
        .setTotal(this.total)
        .setSize(this.size);
    if (this.data != null && !this.data.isEmpty()) {
      List<R> list = this.data.stream().map(mapper)
          .toList();
      out.setData(list);
    }
    return out;
  }
}
