package com.animerec.chat.controllers;

import com.animerec.chat.dto.request.ChatMessageRequest;
import com.animerec.chat.dto.response.ChatSessionResponse;
import com.animerec.chat.services.ChatSessionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/chats")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:80" }, allowCredentials = "true")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    public ChatSessionController(ChatSessionService chatSessionService) {
        this.chatSessionService = chatSessionService;
    }

    @GetMapping
    public ResponseEntity<Page<ChatSessionResponse>> getSessions(Pageable pageable) {
        return ResponseEntity.ok(chatSessionService.getSessions(pageable));
    }

    @PostMapping
    public ResponseEntity<ChatSessionResponse> createSession() {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatSessionService.createSession());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatSessionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(chatSessionService.getById(id));
    }

    @PostMapping(value = "/{id}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(
            @PathVariable UUID id,
            @RequestBody @Valid ChatMessageRequest request) {
        return chatSessionService.sendMessage(id, request.message());
    }
}
