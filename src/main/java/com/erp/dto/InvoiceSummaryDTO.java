package com.erp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InvoiceSummaryDTO {
    private BigDecimal totalSales;      // Sum of all invoices
    private BigDecimal totalPaid;       // Total amount paid
    private BigDecimal totalUnpaid;     // Total outstanding
    private Long totalInvoices;         // Count of invoices
    private Long unpaidInvoices;        // Count of unpaid invoices
}
