package com.erp.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class DashboardOverviewDTO {
    private BigDecimal totalSales;
    private Long lowStockCount;
    private List<InvoiceResponseDTO> recentInvoices;
    private List<StockMovementDTO> recentStock;
}