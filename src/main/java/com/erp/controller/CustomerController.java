package com.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.repository.CustomerRepository;

import entity.Customer;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin
public class CustomerController {
    @Autowired private CustomerRepository customerRepo;

    @GetMapping
    public List<Customer> list() { return customerRepo.findAll(); }

    @PostMapping
    public Customer create(@RequestBody Customer c) { return customerRepo.save(c); }
}