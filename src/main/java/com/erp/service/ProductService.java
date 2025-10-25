package com.erp.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.repository.ProductRepository;
import com.erp.repository.StockMovementRepository;

import entity.Product;
import entity.StockMovement;
import entity.User;

@Service
public class ProductService {
    private final ProductRepository productRepo;
    private final StockMovementRepository stockRepo;

    public ProductService(ProductRepository productRepo, StockMovementRepository stockRepo) {
        this.productRepo = productRepo;
        this.stockRepo = stockRepo;
    }

    public Iterable<Product> listAll() {
        return productRepo.findAll();
    }

    public Optional<Product> find(Long id) { return productRepo.findById(id); }

    public Product save(Product p) { return productRepo.save(p); }

    @Transactional
    public void changeStock(Product product, int qty, String type, String reference, User by) {
        int newStock = product.getStock() + qty;
        product.setStock(newStock);
        productRepo.save(product);

        StockMovement m = new StockMovement();
        m.setProduct(product);
        m.setQty(qty);
        m.setMovementType(type);
        m.setReference(reference);
        m.setCreatedBy(by);
        stockRepo.save(m);
    }
}