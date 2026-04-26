package com.lx.portal.siteconfig;

import com.lx.portal.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class SiteConfig extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String configKey;
    @Column(columnDefinition = "text")
    private String configValue;

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
}

