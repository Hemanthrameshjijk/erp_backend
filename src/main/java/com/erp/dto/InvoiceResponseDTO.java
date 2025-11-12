package com.erp.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data @Getter @Setter
public class InvoiceResponseDTO {
    private Long id;
    private Instant createdAt;
    private String invoiceNo;
    private Long customerId;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String status;
    
    private List<InvoiceItemResponseDTO> items;
    private String createdByUsername;
}