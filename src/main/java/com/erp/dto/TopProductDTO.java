package com.erp.dto;

import lombok.Data;

@Data
public class TopProductDTO {
    private Long productId;
    private String productName;
    private Long qtySold;
}