package com.lx.portal.common;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {
    long countByEventName(String eventName);
}

