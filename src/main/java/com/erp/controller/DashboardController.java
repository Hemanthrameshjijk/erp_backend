package com.erp.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.dto.SalesTrendDTO;
import com.erp.dto.TopProductDTO;
import com.erp.dto.UserPerformanceDTO;
import com.erp.repository.InvoiceItemRepository;
import com.erp.repository.InvoiceRepository;
import com.erp.repository.ProductRepository;
import com.erp.repository.StockMovementRepository;
import com.erp.service.InvoiceService;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class DashboardController {

    @Autowired private InvoiceRepository invoiceRepo;
    @Autowired private InvoiceItemRepository invoiceItemRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private StockMovementRepository stockRepo;

    @Autowired private InvoiceService invoiceService;

    // ✅ Dashboard Overview
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalSales", BigDecimal.valueOf(invoiceRepo.totalSales()));
        data.put("lowStockCount", productRepo.lowStockCount());
        data.put("recentInvoices", invoiceService.getRecentInvoices());
        data.put("recentStock", stockRepo.findTop10ByOrderByCreatedAtDesc());
        return data;
    }


    // ✅ Sales Trend chart
    @GetMapping("/sales-trends")
    public List<SalesTrendDTO> salesTrends() {
        return invoiceRepo.salesTrends().stream().map(r -> {
            SalesTrendDTO dto = new SalesTrendDTO();
            dto.setDate(r[0].toString());
            dto.setTotal((BigDecimal) r[1]);
            return dto;
        }).toList();
    }

    // ✅ Top products list
    @GetMapping("/top-products")
    public List<TopProductDTO> topProducts() {
        return invoiceItemRepo.topSellingProducts().stream().map(r -> {
            TopProductDTO dto = new TopProductDTO();

            // productId
            Object idObj = r[0];
            if (idObj instanceof Number num)
                dto.setProductId(num.longValue());
            else if (idObj instanceof String str)
                dto.setProductId(Long.parseLong(str));

            // productName
            dto.setProductName((String) r[1]);

            // qtySold
            Object qtyObj = r[2];
            if (qtyObj instanceof Number num)
                dto.setQtySold(num.longValue());
            else if (qtyObj instanceof String str)
                dto.setQtySold(Long.parseLong(str));

            return dto;
        }).toList();
    }



// ✅ User performance
 // ✅ User Performance
    @GetMapping("/performance/user/{userId}")
    public UserPerformanceDTO userPerformance(@PathVariable Long userId) {
        UserPerformanceDTO dto = new UserPerformanceDTO();
        dto.setTotalSales(invoiceRepo.userTotalSales(userId));
        dto.setItemsSold(invoiceItemRepo.userTotalItemsSold(userId));
        dto.setStockHandled(stockRepo.userStockHandled(userId)); // Assuming this returns Long
        return dto;
    }

  }