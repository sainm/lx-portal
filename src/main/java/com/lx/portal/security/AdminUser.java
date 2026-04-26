package com.lx.portal.security;

import com.lx.portal.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class AdminUser extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private String displayName;
    @Column(nullable = false)
    private boolean enabled = true;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}

