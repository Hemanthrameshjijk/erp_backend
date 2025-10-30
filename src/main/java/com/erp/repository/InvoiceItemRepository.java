package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entity.InvoiceItem;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
	List<InvoiceItem> findByInvoiceId(Long invoiceId);
	
	//
	  @Query("SELECT ii.product.name, SUM(ii.qty) " +
	           "FROM InvoiceItem ii GROUP BY ii.product.id ORDER BY SUM(ii.qty) DESC")
	    List<Object[]> topSellingProducts();

	    @Query("SELECT SUM(ii.qty) FROM InvoiceItem ii WHERE ii.invoice.createdBy.id = :userId")
	    Integer userTotalItemsSold(Long userId);
}
