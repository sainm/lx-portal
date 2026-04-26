package com.lx.portal.chat;

import com.lx.portal.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ChatMessage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessageRole role;
    @Column(nullable = false, columnDefinition = "text")
    private String content;
    @Column(nullable = false)
    private boolean crisisFlagged;

    public ChatSession getSession() { return session; }
    public void setSession(ChatSession session) { this.session = session; }
    public ChatMessageRole getRole() { return role; }
    public void setRole(ChatMessageRole role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isCrisisFlagged() { return crisisFlagged; }
    public void setCrisisFlagged(boolean crisisFlagged) { this.crisisFlagged = crisisFlagged; }
}

