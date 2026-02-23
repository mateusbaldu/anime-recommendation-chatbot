package com.animerec.chat.controllers;

import com.animerec.chat.dto.request.ChatMessageRequest;
import com.animerec.chat.dto.response.ChatSessionResponse;
import com.animerec.chat.services.ChatSessionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chats")
public class ChatSessionController {

    private static final Logger logger = LoggerFactory.getLogger(ChatSessionController.class);
    private final ChatSessionService chatSessionService;

    public ChatSessionController(ChatSessionService chatSessionService) {
        this.chatSessionService = chatSessionService;
    }

    @GetMapping
    public ResponseEntity<?> getSessions(Pageable pageable) {
        try {
            logger.info("Fetching chat sessions");
            return ResponseEntity.ok(chatSessionService.getSessions(pageable));
        } catch (Exception e) {
            logger.error("Error fetching sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch sessions: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createSession() {
        try {
            logger.info("Creating a new chat session");
            return ResponseEntity.status(HttpStatus.CREATED).body(chatSessionService.createSession());
        } catch (Exception e) {
            logger.error("Error creating chat session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create chat session. Please try again later."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            logger.info("Fetching chat session: {}", id);
            return ResponseEntity.ok(chatSessionService.getById(id));
        } catch (Exception e) {
            logger.error("Error fetching chat session: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Chat session not found or access denied"));
        }
    }

    @PostMapping(value = "/{id}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendMessage(
            @PathVariable UUID id,
            @RequestBody @Valid ChatMessageRequest request) {
        try {
            logger.info("Received message for session: {}", id);
            return chatSessionService.sendMessage(id, request.message());
        } catch (Exception e) {
            logger.error("Error initiating message send for session: {}", id, e);
            SseEmitter emitter = new SseEmitter();
            emitter.completeWithError(e);
            return emitter;
        }
    }
}
