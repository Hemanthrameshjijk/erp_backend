package com.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.repository.InvoiceItemRepository;
import com.erp.repository.InvoiceRepository;
import com.erp.repository.ProductRepository;
import com.erp.repository.StockMovementRepository;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class DashboardController {

    @Autowired private InvoiceRepository invoiceRepo;
    @Autowired private InvoiceItemRepository invoiceItemRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private StockMovementRepository stockRepo;

    // ✅ GET /overview
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalSales", invoiceRepo.totalSales());
        data.put("lowStockCount", productRepo.lowStockCount());
        data.put("recentInvoices", invoiceRepo.findTop10ByOrderByCreatedAtDesc());
        data.put("recentStock", stockRepo.findTop10ByOrderByCreatedAtDesc());
        return data;
    }

    // ✅ GET /sales-trends
    @GetMapping("/sales-trends")
    public List<Object[]> salesTrends() {
        return invoiceRepo.salesTrends();
    }

    // ✅ GET /top-products
    @GetMapping("/top-products")
    public List<Object[]> topProducts() {
        return invoiceItemRepo.topSellingProducts();
    }

    // ✅ GET /activity
    @GetMapping("/activity")
    public Map<String, Object> recentActivity() {
        Map<String, Object> data = new HashMap<>();
        data.put("recentInvoices", invoiceRepo.findTop10ByOrderByCreatedAtDesc());
        data.put("recentStock", stockRepo.findTop10ByOrderByCreatedAtDesc());
        return data;
    }

    // ✅ GET /performance/user/{userId}
    @GetMapping("/performance/user/{userId}")
    public Map<String, Object> userPerformance(@PathVariable Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("totalSales", invoiceRepo.userTotalSales(userId));
        data.put("itemsSold", invoiceItemRepo.userTotalItemsSold(userId));
        data.put("stockHandled", stockRepo.userStockHandled(userId));
        return data;
    }
}