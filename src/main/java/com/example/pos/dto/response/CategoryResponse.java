package com.example.pos.dto.response;

import com.example.pos.entitiy.category.Category;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CategoryResponse {
    UUID categoryId;

    String name;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .build();
    }
}
