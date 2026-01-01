package com.example.pos.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {

    List<T> items;

    int page;
    int size;

    long totalItems;
    int totalPages;

    boolean hasNext;
    boolean hasPrevious;
}
