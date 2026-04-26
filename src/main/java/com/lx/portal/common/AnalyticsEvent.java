package com.lx.portal.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class AnalyticsEvent extends BaseEntity {
    @Column(nullable = false)
    private String eventName;
    private String pagePath;
    private String target;

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getPagePath() { return pagePath; }
    public void setPagePath(String pagePath) { this.pagePath = pagePath; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
}

