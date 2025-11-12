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
	@Query("""
		    SELECT p.id, p.name, SUM(ii.qty)
		    FROM InvoiceItem ii
		    JOIN ii.product p
		    GROUP BY p.id, p.name
		    ORDER BY SUM(ii.qty) DESC
		    """)
		List<Object[]> topSellingProducts();


	    @Query("SELECT COALESCE(SUM(ii.qty), 0) FROM InvoiceItem ii WHERE ii.invoice.createdBy.id = :userId")
	    Long userTotalItemsSold(Long userId);
	
}
