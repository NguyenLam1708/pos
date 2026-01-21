package com.example.pos.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateProductRequest {
    String name;
    UUID categoryId;
    long price;
}
