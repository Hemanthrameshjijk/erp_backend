package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
	List<Invoice> findByCustomerId(Long customerId);
    List<Invoice> findByStatus(String status);
    
    @Query("SELECT SUM(totalAmount) as totalSales, SUM(totalAmount - paidAmount) as unpaid FROM Invoice")   
    Object getSummary();
    
    //
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status='PAID'")
    Double totalSales();

    @Query("SELECT DATE(i.createdAt) AS day, SUM(i.totalAmount) " +
           "FROM Invoice i WHERE i.status='PAID' GROUP BY DATE(i.createdAt)")
    List<Object[]> salesTrends();

    List<Invoice> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.createdBy.id = :userId AND i.status='PAID'")
    Double userTotalSales(Long userId);
}