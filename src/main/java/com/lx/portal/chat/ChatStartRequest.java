package com.lx.portal.chat;

public record ChatStartRequest(
        String visitorName,
        String visitorContact,
        String topic
) {
}

