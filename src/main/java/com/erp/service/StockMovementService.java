package com.erp.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	public StockMovementRepository stockRepo;
    @Autowired ProductRepository productRepo;
    

    public List<StockMovement> getByProduct(Long productId) {
        return stockRepo.findByProductIdOrderByCreatedAtDesc(productId);
    }
    public ResponseEntity<?> getById(Long id) {
        return stockRepo.findById(id)
            .<ResponseEntity<?>>map(sm -> ResponseEntity.ok(sm))
            .orElse(ResponseEntity.status(404).body(Map.of("error", "Stock movement not found")));
    }

    public List<StockMovement> getByUser(Long userId) {
        return stockRepo.findByCreatedByIdOrderByCreatedAtDesc(userId);
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

    public List<StockMovement> getRecent() {
        return stockRepo.findRecentMovements();
    }
}
