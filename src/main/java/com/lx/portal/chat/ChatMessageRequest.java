package com.lx.portal.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank(message = "消息不能为空") String content,
        String visitorName,
        String visitorContact,
        String topic
) {
}

