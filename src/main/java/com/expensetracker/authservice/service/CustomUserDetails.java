package com.expensetracker.authservice.service;

import com.expensetracker.authservice.entity.UserInfo;
import com.expensetracker.authservice.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends UserInfo implements UserDetails {

    private String username;
    private String password;

    public CustomUserDetails(UserInfo byUserName){
        this.username = byUserName.getUsername();
        this.password = byUserName.getPassword();
        List<GrantedAuthority> auths = new ArrayList<>();
        for(UserRole role : byUserName.getRoles()) {
            auths.add(new SimpleGrantedAuthority(role.getRoleName().toUpperCase()));
        }
        this.authorities = auths;
    }

    Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public String getPassword() {
        return password;
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
}
