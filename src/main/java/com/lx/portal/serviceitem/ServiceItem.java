package com.lx.portal.serviceitem;

import com.lx.portal.common.BaseEntity;
import com.lx.portal.common.PublishStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class ServiceItem extends BaseEntity {
    @Column(nullable = false)
    private String name;
    private String summary;
    private String suitableFor;
    private String commonTopics;
    private String counselingMethods;
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
    @Column(nullable = false)
    private int sortOrder;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublishStatus status = PublishStatus.DRAFT;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getSuitableFor() { return suitableFor; }
    public void setSuitableFor(String suitableFor) { this.suitableFor = suitableFor; }
    public String getCommonTopics() { return commonTopics; }
    public void setCommonTopics(String commonTopics) { this.commonTopics = commonTopics; }
    public String getCounselingMethods() { return counselingMethods; }
    public void setCounselingMethods(String counselingMethods) { this.counselingMethods = counselingMethods; }
    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }
    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public PublishStatus getStatus() { return status; }
    public void setStatus(PublishStatus status) { this.status = status; }
}

