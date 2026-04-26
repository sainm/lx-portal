package com.lx.portal.upload;

import com.lx.portal.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class UploadFile extends BaseEntity {
    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false)
    private String storedName;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private long sizeBytes;
    @Column(nullable = false)
    private String publicUrl;

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getPublicUrl() { return publicUrl; }
    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }
}

