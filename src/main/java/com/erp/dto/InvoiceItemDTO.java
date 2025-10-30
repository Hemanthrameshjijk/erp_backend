package com.erp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InvoiceItemDTO {
    private Long id;
    private Long invoiceId;
    private Long productId;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
