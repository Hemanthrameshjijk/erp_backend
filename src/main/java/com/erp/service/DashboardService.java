package com.erp.service;

public interface DashboardService {
    Object getOverview();
    Object getSalesTrends();
    Object getTopProducts();
    Object getRecentActivity();
    Object getUserPerformance(Long userId);
}
