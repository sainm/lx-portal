package com.lx.portal.chat;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {
    private static final Pattern CRISIS_PATTERN = Pattern.compile("自杀|自伤|轻生|伤害自己|不想活|活不下去|伤人|杀人");
    private static final String WELCOME = "你好，我是预约助手。这里可以帮你初步整理咨询方向并留下联系方式；我不能提供诊断、治疗或急救服务。如果有立即危险，请马上联系当地急救、警方或可信赖的人。";
    private static final String CRISIS_REPLY = "我注意到你提到可能存在紧急风险。网页聊天不能提供急救或危机干预。如果你或身边的人有立即危险，请立刻联系当地急救、警方或可信赖的人，并尽快获得线下支持。";

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final AiChatProvider aiChatProvider;

    public ChatService(ChatSessionRepository sessionRepository, ChatMessageRepository messageRepository,
            AiChatProvider aiChatProvider) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.aiChatProvider = aiChatProvider;
    }

    @Transactional
    public ChatSessionDto start(ChatStartRequest request) {
        ChatSession session = new ChatSession();
        session.setVisitorName(request.visitorName());
        session.setVisitorContact(request.visitorContact());
        session.setTopic(request.topic());
        session.setAiEnabled(aiChatProvider.enabled());
        ChatSession saved = sessionRepository.save(session);
        addMessage(saved, ChatMessageRole.ASSISTANT, WELCOME, false);
        return get(saved.getId());
    }

    @Transactional
    public ChatSessionDto send(Long sessionId, ChatMessageRequest request) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("聊天会话不存在"));
        if (request.visitorName() != null && !request.visitorName().isBlank()) {
            session.setVisitorName(request.visitorName());
        }
        if (request.visitorContact() != null && !request.visitorContact().isBlank()) {
            session.setVisitorContact(request.visitorContact());
            session.setStatus(ChatSessionStatus.WAITING_CONTACT);
        }
        if (request.topic() != null && !request.topic().isBlank()) {
            session.setTopic(request.topic());
        }
        boolean crisis = containsCrisis(request.content());
        if (crisis) {
            session.setCrisisFlagged(true);
        }
        addMessage(session, ChatMessageRole.VISITOR, request.content(), crisis);
        List<ChatMessage> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        String reply = crisis ? CRISIS_REPLY : aiChatProvider.generateReply(session, messages, request.content());
        addMessage(session, ChatMessageRole.ASSISTANT, reply, crisis);
        return get(sessionId);
    }

    @Transactional(readOnly = true)
    public ChatSessionDto get(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("聊天会话不存在"));
        return ChatSessionDto.from(session, messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId));
    }

    private ChatMessage addMessage(ChatSession session, ChatMessageRole role, String content, boolean crisis) {
        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setRole(role);
        message.setContent(content);
        message.setCrisisFlagged(crisis);
        return messageRepository.save(message);
    }

    private boolean containsCrisis(String content) {
        return content != null && CRISIS_PATTERN.matcher(content).find();
    }
}

