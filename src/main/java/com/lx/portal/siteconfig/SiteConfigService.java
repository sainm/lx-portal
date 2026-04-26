package com.lx.portal.siteconfig;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteConfigService {
    private final SiteConfigRepository repository;

    public SiteConfigService(SiteConfigRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Map<String, String> all() {
        return repository.findAll().stream()
                .collect(Collectors.toMap(SiteConfig::getConfigKey, c -> c.getConfigValue() == null ? "" : c.getConfigValue()));
    }

    @Transactional
    public SiteConfig put(String key, String value) {
        SiteConfig config = repository.findByConfigKey(key).orElseGet(SiteConfig::new);
        config.setConfigKey(key);
        config.setConfigValue(value);
        return repository.save(config);
    }
}

