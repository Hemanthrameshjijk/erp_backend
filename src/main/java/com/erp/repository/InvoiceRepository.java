package com.erp.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    @Query("SELECT DISTINCT i FROM Invoice i " +
            "LEFT JOIN FETCH i.items it " +
            "LEFT JOIN FETCH it.product " +
            "LEFT JOIN FETCH i.customer " +
            "LEFT JOIN FETCH i.createdBy")
     List<Invoice> findAllWithDetails();
    
    @Query("SELECT DISTINCT i FROM Invoice i LEFT JOIN FETCH i.items WHERE i.customer.id = :customerId")
    List<Invoice> findByCustomerWithItems(@Param("customerId") Long customerId);
    
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i")
    BigDecimal getTotalSales();

    // 🟩 Top-selling products: product ID, name, and total quantity sold
    @Query("""
    	       SELECT p.id, p.name, SUM(ii.qty)
    	       FROM InvoiceItem ii
    	       JOIN ii.product p
    	       GROUP BY p.id, p.name
    	       ORDER BY SUM(ii.qty) DESC
    	       """)
    	List<Object[]> topSellingProducts();


    // 🟩 Total items sold by a user
    @Query("SELECT SUM(ii.qty) FROM InvoiceItem ii WHERE ii.invoice.createdBy.id = :userId")
    Long userTotalItemsSold(Long userId);
    
    

}