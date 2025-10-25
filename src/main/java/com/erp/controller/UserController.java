package com.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.repository.UserRepository;

import entity.User;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    @Autowired private UserRepository userRepo;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() { return userRepo.findAll(); }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User get(@PathVariable Long id) { return userRepo.findById(id).orElse(null); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User create(@RequestBody User u) { return userRepo.save(u); }
}