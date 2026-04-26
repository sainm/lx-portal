package com.lx.portal.chat;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class NoopAiChatProvider implements AiChatProvider {
    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public String generateReply(ChatSession session, List<ChatMessage> messages, String latestVisitorMessage) {
        return "我先帮你记录下来。为了更准确地安排咨询，请留下称呼、联系方式、困扰方向和偏好时间，工作人员会尽快联系你。";
    }
}
