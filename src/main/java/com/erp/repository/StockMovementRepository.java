package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entity.StockMovement;
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
	  List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

	    List<StockMovement> findByCreatedByIdOrderByCreatedAtDesc(Long userId);


	    @Query(value = "SELECT * FROM stock_movements ORDER BY created_at DESC LIMIT 10", nativeQuery = true)
	    List<StockMovement> findRecentMovements();
	    
	    List<StockMovement> findTop10ByOrderByCreatedAtDesc();
	    
	    @Query("SELECT SUM(s.qty) FROM StockMovement s WHERE s.createdBy.id = :userId")
	    Integer userStockHandled(Long userId);
	}
	
