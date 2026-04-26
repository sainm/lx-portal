package com.lx.portal.chat;

import java.util.List;

public interface AiChatProvider {
    boolean enabled();
    String generateReply(ChatSession session, List<ChatMessage> messages, String latestVisitorMessage);
}

