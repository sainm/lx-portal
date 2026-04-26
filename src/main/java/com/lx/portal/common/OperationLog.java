package com.lx.portal.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class OperationLog extends BaseEntity {
    private String actor;
    @Column(nullable = false)
    private String action;
    private String targetType;
    private Long targetId;
    @Column(columnDefinition = "text")
    private String detail;

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}

