package com.example.pos.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProductRequest {
    String name;
    UUID categoryId;
    Long price;
}
