package com.erp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @Autowired private PasswordEncoder passwordEncoder; 

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() { return userRepo.findAll(); }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User get(@PathVariable Long id) { return userRepo.findById(id).orElse(null); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User create(@RequestBody User u) { return userRepo.save(u); }    
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updateData) {

        User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only update fields you allow
        if (updateData.getUsername() != null) {
            existingUser.setUsername(updateData.getUsername());
        }

        if (updateData.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(updateData.getPassword()));
        }

        if (updateData.getRole() != null) {
            existingUser.setRole(updateData.getRole());
        }

        userRepo.save(existingUser);

        return ResponseEntity.ok(Map.of(
                "message", "User updated successfully",
                "id", existingUser.getId(),
                "username", existingUser.getUsername(),
                "role", existingUser.getRole().getName()
        ));
    }
}