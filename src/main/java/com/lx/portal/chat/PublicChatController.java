package com.lx.portal.chat;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.portal.common.ApiResponse;

@RestController
@RequestMapping("/api/public/chat")
public class PublicChatController {
    private final ChatService chatService;

    public PublicChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/sessions")
    public ApiResponse<ChatSessionDto> start(@RequestBody ChatStartRequest request) {
        return ApiResponse.ok(chatService.start(request));
    }

    @GetMapping("/sessions/{sessionId}")
    public ApiResponse<ChatSessionDto> get(@PathVariable Long sessionId) {
        return ApiResponse.ok(chatService.get(sessionId));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ApiResponse<ChatSessionDto> send(@PathVariable Long sessionId, @Valid @RequestBody ChatMessageRequest request) {
        return ApiResponse.ok(chatService.send(sessionId, request));
    }
}
