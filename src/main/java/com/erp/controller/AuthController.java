package com.erp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.dto.AuthRequest;
import com.erp.dto.AuthResponse;
import com.erp.repository.UserRepository;
import com.erp.security.JwtUtil;

import entity.User;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired private AuthenticationManager authManager;
    @Autowired private UserRepository userRepo;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            // Attempt authentication
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (Exception e) {
            // Log full exception for debugging
            e.printStackTrace();
            // Return 401 Unauthorized with error message
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Login failed",
                            "message", e.getMessage()
                    ));
        }

        // Authentication successful, fetch user from DB
        User u = userRepo.findByUsername(req.getUsername())
                         .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        // Generate JWT
        String jwt = jwtUtil.generateToken(u);

        // Return JWT and user info
        AuthResponse response = new AuthResponse(jwt, u.getUsername(), u.getRole() == null ? null : u.getRole().getName());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest req) {
    	if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username exists");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        // default to MANAGER role if exists
        var roleOpt = userRepo.findAll().stream().findFirst().flatMap(x -> java.util.Optional.ofNullable(x.getRole()));
        u.setRole(roleOpt.orElse(null));
        userRepo.save(u);
        String jwt = jwtUtil.generateToken(u);
        return new AuthResponse(jwt, u.getUsername(), u.getRole()==null?null:u.getRole().getName());


    }
    
    @GetMapping("/status")
    public String status() { return "auth ok"; }
}