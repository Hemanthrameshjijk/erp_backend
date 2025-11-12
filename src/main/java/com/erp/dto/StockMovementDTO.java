package com.erp.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class StockMovementDTO {
    private Long id;
    private String productName;
    private Integer qty;
    private String movementType;
    private String reference;
    private Instant createdAt;
}