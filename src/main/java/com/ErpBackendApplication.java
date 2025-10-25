package com;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.erp.repository.RoleRepository;
import com.erp.repository.UserRepository;

import entity.Role;
import entity.User;

@SpringBootApplication
@ComponentScan(basePackages = {"com", "com.erp.controller", "entity", "com.erp.repository", "com.erp.service","config"})
@EntityScan(basePackages = {"entity"})

public class ErpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo) {
        return args -> {
            if (roleRepo.count() == 0) {
                Role admin = roleRepo.save(new Role("ROLE_ADMIN"));
                Role manager = roleRepo.save(new Role("ROLE_MANAGER"));
                Role cashier = roleRepo.save(new Role("ROLE_CASHIER"));

                if (userRepo.count() == 0) {
                    userRepo.save(new User("admin", "$2a$12$hBuoF9srEyjvUEdhfY9Xe.z60XHI88d7blvJcruZevpSe0BAWb56i", "Admin User", "admin@erp.local", admin));
                    // password 'admin123' hashed with BCrypt example
                    userRepo.save(new User("manager", "$2a$10$6kXQz1f8XKJ8v7v1yQzZ.e6b6YQ6wqHn1Z5u5Zr3YVqg8Zb1lF2eK", "Manger User", "manager@erp.local", manager));
                    userRepo.save(new User("cashier", "$2a$10$6kXQz1f8XKJ8v7v1yQzZ.e6b6YQ6wqHn1Z5u5Zr3YVqg8Zb1lF2eK", "Cashier User", "cashier@erp.local", cashier));

                }
            }
        };
    }
}

