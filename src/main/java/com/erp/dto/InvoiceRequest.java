package com.erp.dto;

import java.util.List;


import lombok.Data;

@Data
public class InvoiceRequest {
    private Long customerId;
    private List<InvoiceItemDTO> items;
    private String status; // Optional on update
}

