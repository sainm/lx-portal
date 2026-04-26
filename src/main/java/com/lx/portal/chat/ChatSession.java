package com.lx.portal.chat;

import com.lx.portal.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class ChatSession extends BaseEntity {
    private String visitorName;
    private String visitorContact;
    private String topic;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatSessionStatus status = ChatSessionStatus.OPEN;
    @Column(nullable = false)
    private boolean crisisFlagged;
    @Column(nullable = false)
    private boolean aiEnabled;

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public String getVisitorContact() { return visitorContact; }
    public void setVisitorContact(String visitorContact) { this.visitorContact = visitorContact; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public ChatSessionStatus getStatus() { return status; }
    public void setStatus(ChatSessionStatus status) { this.status = status; }
    public boolean isCrisisFlagged() { return crisisFlagged; }
    public void setCrisisFlagged(boolean crisisFlagged) { this.crisisFlagged = crisisFlagged; }
    public boolean isAiEnabled() { return aiEnabled; }
    public void setAiEnabled(boolean aiEnabled) { this.aiEnabled = aiEnabled; }
}

