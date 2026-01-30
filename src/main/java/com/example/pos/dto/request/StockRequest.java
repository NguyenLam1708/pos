package com.example.pos.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class StockRequest {
    UUID productId;
    int quantity;
}
