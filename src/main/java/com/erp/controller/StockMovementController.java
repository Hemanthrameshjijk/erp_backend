package com.erp.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.repository.ProductRepository;
import com.erp.repository.UserRepository;
import com.erp.service.StockMovementService;

import entity.Product;
import entity.StockMovement;
import entity.User;

@RestController
@RequestMapping("/api/stock-movements")
@CrossOrigin
public class StockMovementController {

    @Autowired StockMovementService movementService;
    @Autowired ProductRepository productRepo;
    @Autowired UserRepository userRepo;
        // ✅ Get all stock movements
        @GetMapping
        public List<StockMovement> getAll() {
            return movementService.getAll();
        }

        // ✅ Get specific stock movement by ID
        @GetMapping("/{id}")
        public StockMovement getById(@PathVariable Long id) {
            return movementService.getById(id);
        }

        // ✅ Get stock movements created by a specific user
        @GetMapping("/user/{userId}")
        public List<StockMovement> getByUser(@PathVariable Long userId) {
            return movementService.getByUserId(userId);
        }

        // ✅ Get recent (latest 10) stock movements
        @GetMapping("/recent")
        public List<StockMovement> getRecent() {
            return movementService.getRecent();
        }

        // ✅ Create new stock movement
        @PostMapping
        public StockMovement create(@RequestBody StockMovement movement) {
            return movementService.recordMovement(movement);
        }

        // ✅ Count total handled by user
        @GetMapping("/user/{userId}/count")
        public Long countUserStock(@PathVariable Long userId) {
            return movementService.getUserStockHandled(userId);
        }
        
        @PostMapping("/in")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        public ResponseEntity<?> stockIn(@RequestBody Map<String,Object> req, Principal principal) {
            Long productId = Long.valueOf(req.get("productId").toString());
            Integer qty = Integer.valueOf(req.get("qty").toString());
            String ref = req.get("reference") != null ? req.get("reference").toString() : null;

            Product p = productRepo.findById(productId).orElseThrow();
            User u = userRepo.findByUsername(principal.getName()).orElseThrow();

            return ResponseEntity.ok(movementService.recordMovement(p, qty, "IN", ref, u));
        }

        @PostMapping("/out")
        @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
        public ResponseEntity<?> stockOut(@RequestBody Map<String,Object> req, Principal principal) {
            Long productId = Long.valueOf(req.get("productId").toString());
            Integer qty = Integer.valueOf(req.get("qty").toString());
            String ref = req.get("reference") != null ? req.get("reference").toString() : null;

            Product p = productRepo.findById(productId).orElseThrow();
            User u = userRepo.findByUsername(principal.getName()).orElseThrow();

            return ResponseEntity.ok(movementService.recordMovement(p, qty, "OUT", ref, u));
        }
    }