package com.example.pos.util;

import com.example.pos.dto.request.common.CommonPaginateRequest;
import io.quarkus.panache.common.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestHelper {

  public static Page toPage(CommonPaginateRequest request) {
    var pageInput = request.getPaginateInput();
    return Page.of(pageInput.getPage() - 1, pageInput.getSize());

  }
}
