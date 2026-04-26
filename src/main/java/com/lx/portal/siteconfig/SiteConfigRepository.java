package com.lx.portal.siteconfig;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteConfigRepository extends JpaRepository<SiteConfig, Long> {
    Optional<SiteConfig> findByConfigKey(String configKey);
}

