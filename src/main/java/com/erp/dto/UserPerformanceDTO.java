package com.erp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserPerformanceDTO {
    private Double  totalSales;
    private Long itemsSold;
    private Long stockHandled;
}