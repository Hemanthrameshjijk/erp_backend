package com.erp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesTrendDTO {
    private String date;
    private BigDecimal total;
}