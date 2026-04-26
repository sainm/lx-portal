package com.lx.portal.counselor;

import com.lx.portal.common.BaseEntity;
import com.lx.portal.common.PublishStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Counselor extends BaseEntity {
    @Column(nullable = false)
    private String name;
    private String avatarUrl;
    private String summary;
    private String specialties;
    private String serviceAudience;
    private String counselingMethods;
    private String counselingStyle;
    private String priceAndDuration;
    @Column(columnDefinition = "text")
    private String qualifications;
    @Column(nullable = false)
    private int sortOrder;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublishStatus status = PublishStatus.DRAFT;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getSpecialties() { return specialties; }
    public void setSpecialties(String specialties) { this.specialties = specialties; }
    public String getServiceAudience() { return serviceAudience; }
    public void setServiceAudience(String serviceAudience) { this.serviceAudience = serviceAudience; }
    public String getCounselingMethods() { return counselingMethods; }
    public void setCounselingMethods(String counselingMethods) { this.counselingMethods = counselingMethods; }
    public String getCounselingStyle() { return counselingStyle; }
    public void setCounselingStyle(String counselingStyle) { this.counselingStyle = counselingStyle; }
    public String getPriceAndDuration() { return priceAndDuration; }
    public void setPriceAndDuration(String priceAndDuration) { this.priceAndDuration = priceAndDuration; }
    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public PublishStatus getStatus() { return status; }
    public void setStatus(PublishStatus status) { this.status = status; }
}

