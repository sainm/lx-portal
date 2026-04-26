package com.lx.portal.chat;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long id,
        ChatMessageRole role,
        String content,
        boolean crisisFlagged,
        LocalDateTime createdAt
) {
    public static ChatMessageDto from(ChatMessage message) {
        return new ChatMessageDto(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.isCrisisFlagged(),
                message.getCreatedAt());
    }
}

