package com.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.repository.ProductRepository;

import entity.Product;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
    @Autowired private ProductRepository productRepo;

    @GetMapping
    public List<Product> list() { return productRepo.findAll(); }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) { return productRepo.findById(id).orElse(null); }

    @PostMapping
    public Product create(@RequestBody Product p) { return productRepo.save(p); }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id,@RequestBody Product p) {
        p.setId(id);
        return productRepo.save(p);
    }
}