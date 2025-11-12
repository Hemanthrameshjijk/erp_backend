package com.erp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.repository.ProductRepository;
import com.erp.repository.StockMovementRepository;

import entity.Product;
import entity.StockMovement;
import entity.User;

@Service
public class StockMovementService {

	 @Autowired
	    private StockMovementRepository stockRepo;
	 	private ProductRepository productRepo;
	    // ✅ Fetch all stock movements
	    public List<StockMovement> getAll() {
	        return stockRepo.findAll();
	    }

	    // ✅ Fetch by ID
	    public StockMovement getById(Long id) {
	        return stockRepo.findById(id)
	                .orElseThrow(() -> new RuntimeException("Stock Movement not found with ID: " + id));
	    }

	    // ✅ Fetch by User ID
	    public List<StockMovement> getByUserId(Long userId) {
	        return stockRepo.findByCreatedByIdOrderByCreatedAtDesc(userId);
	    }

	    // ✅ Save or record new stock movement
	    public StockMovement recordMovement(StockMovement movement) {
	        return stockRepo.save(movement);
	    }

	    // ✅ Recent movements (limit top 10)
	    public List<StockMovement> getRecent() {
	        return stockRepo.findTop10ByOrderByCreatedAtDesc();
	    }

	    // ✅ Count stock movements handled by a user
	    public Long getUserStockHandled(Long userId) {
	        return stockRepo.userStockHandled(userId);
	    }
	    @Transactional
	    public StockMovement recordMovement(Product product, Integer qty, String type, String ref, User user) {
	        StockMovement sm = new StockMovement();
	        sm.setProduct(product);
	        sm.setQty(qty);
	        sm.setMovementType(type);
	        sm.setReference(ref);
	        sm.setCreatedBy(user);

	        // Update product stock
	        if (type.equals("IN")) {
	            product.setStock(product.getStock() + qty);
	        } else {
	            product.setStock(product.getStock() - qty);

	            if (product.getStock() < 0) throw new RuntimeException("Stock cannot be negative");
	        }

	        productRepo.save(product);
	        return stockRepo.save(sm);
	    }
	}
