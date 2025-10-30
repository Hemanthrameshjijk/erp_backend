package com.erp.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import entity.User;

public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private User user;

    public CustomUserDetails(User user) { 
        this.user = user; 
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole() != null ? user.getRole().getName() : "";

        // ✅ If DB already has ROLE_ prefix, don't add again
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        return Collections.singleton(new SimpleGrantedAuthority(roleName));
    }


    @Override
    public String getPassword() { 
        return user.getPassword(); 
    }

    @Override
    public String getUsername() { 
        return user.getUsername(); 
    }

    @Override
    public boolean isAccountNonExpired() { 
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() { 
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() { 
        return true; 
    }

    @Override
    public boolean isEnabled() { 
        return true; 
    }

    public User getUser() { 
        return user; 
    }
}
