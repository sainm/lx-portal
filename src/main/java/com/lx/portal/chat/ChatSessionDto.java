package com.lx.portal.chat;

import java.time.LocalDateTime;
import java.util.List;

public record ChatSessionDto(
        Long id,
        String visitorName,
        String visitorContact,
        String topic,
        ChatSessionStatus status,
        boolean crisisFlagged,
        boolean aiEnabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ChatMessageDto> messages
) {
    public static ChatSessionDto from(ChatSession session, List<ChatMessage> messages) {
        return new ChatSessionDto(
                session.getId(),
                session.getVisitorName(),
                session.getVisitorContact(),
                session.getTopic(),
                session.getStatus(),
                session.isCrisisFlagged(),
                session.isAiEnabled(),
                session.getCreatedAt(),
                session.getUpdatedAt(),
                messages.stream().map(ChatMessageDto::from).toList());
    }
}

