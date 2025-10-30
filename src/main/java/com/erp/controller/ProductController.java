package com.erp.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.erp.repository.ProductRepository;
import entity.Product;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired 
    private ProductRepository productRepo;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Product> list() {
        return productRepo.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return productRepo.findById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(404)
                .body(Map.of("error", "Product not found")));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> create(@RequestBody Product p) {
        return ResponseEntity.ok(productRepo.save(p));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product p) {
        return productRepo.findById(id)
            .<ResponseEntity<?>>map(existing -> {
                if (p.getName() != null) existing.setName(p.getName());
                if (p.getSku() != null) existing.setSku(p.getSku());
                if (p.getDescription() != null) existing.setDescription(p.getDescription());
                if (p.getCostPrice() != null) existing.setCostPrice(p.getCostPrice());
                if (p.getSellPrice() != null) existing.setSellPrice(p.getSellPrice());
                if (p.getStock() != null) existing.setStock(p.getStock());
                if (p.getReorderLevel() != null) existing.setReorderLevel(p.getReorderLevel());
                
                productRepo.save(existing);
                return ResponseEntity.ok(existing);
            })
            .orElseGet(() -> ResponseEntity.status(404)
                .body(Map.of("error", "Product not found")));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!productRepo.existsById(id)) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "Product not found"));
        }
        productRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted"));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Product> lowStock() {
        return productRepo.findProductsLowInStock();
    }

    @PutMapping("/{id}/adjust-stock")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> adjustStock(
            @PathVariable Long id, 
            @RequestBody Map<String,Integer> req) {

        int qty = req.get("quantity");

        return productRepo.findById(id).map(p -> {
            p.setStock(p.getStock() + qty);
            productRepo.save(p);
            return ResponseEntity.ok(Map.of(
                "message", "Stock updated",
                "newStock", p.getStock()
            ));
        }).orElse(ResponseEntity.status(404)
            .body(Map.of("error", "Product not found")));
    }
}
