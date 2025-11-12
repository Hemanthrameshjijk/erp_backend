package com.erp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class InvoiceItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}