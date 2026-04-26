package com.lx.portal.article;

import com.lx.portal.common.BaseEntity;
import com.lx.portal.common.PublishStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class ArticleCategory extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String slug;
    @Column(nullable = false)
    private int sortOrder;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PublishStatus status = PublishStatus.DRAFT;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public PublishStatus getStatus() { return status; }
    public void setStatus(PublishStatus status) { this.status = status; }
}

