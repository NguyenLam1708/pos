package com.example.pos.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddOrderItemRequest {

    @NotNull
    UUID productId;

    @Min(1)
    int quantity;

    String notes;
}
