package com.erp.service;



import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public Object getOverview() {
        // -> total sales, total customers, low stock count
        return null;
    }

    @Override
    public Object getSalesTrends() {
        // -> sales by day/month/week
        return null;
    }

    @Override
    public Object getTopProducts() {
        // -> top selling products with quantities
        return null;
    }

    @Override
    public Object getRecentActivity() {
        // -> recent invoices + stock movements
        return null;
    }

    @Override
    public Object getUserPerformance(Long userId) {
        // -> user's sales, stock movement count, revenue handled
        return null;
    }
}
