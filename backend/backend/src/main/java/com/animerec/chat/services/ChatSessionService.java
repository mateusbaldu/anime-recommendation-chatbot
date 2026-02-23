package com.animerec.chat.services;

import com.animerec.chat.dto.request.ChatMessage;
import com.animerec.chat.dto.response.ChatSessionResponse;
import com.animerec.chat.models.ChatSession;
import com.animerec.chat.models.User;
import com.animerec.chat.repositories.ChatSessionRepository;
import com.animerec.chat.repositories.UserRepository;
import com.animerec.chat.security.AuthenticationProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ChatSessionService {

    private static final Logger logger = LoggerFactory.getLogger(ChatSessionService.class);
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final AuthenticationProvider authenticationProvider;
    private final AIService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ChatSessionService(
            ChatSessionRepository chatSessionRepository,
            UserRepository userRepository,
            AuthenticationProvider authenticationProvider,
            AIService aiService) {
        this.chatSessionRepository = chatSessionRepository;
        this.userRepository = userRepository;
        this.authenticationProvider = authenticationProvider;
        this.aiService = aiService;
    }

    private UUID getCurrentUserId() {
        return authenticationProvider.getCurrentUserId();
    }

    public Page<ChatSessionResponse> getSessions(Pageable pageable) {
        try {
            UUID userId = getCurrentUserId();
            logger.info("Fetching sessions for user: {}", userId);
            return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable)
                    .map(this::toResponse);
        } catch (Exception e) {
            logger.error("Error fetching sessions", e);
            throw e;
        }
    }

    @Transactional
    public ChatSessionResponse createSession() {
        try {
            UUID userId = getCurrentUserId();
            logger.info("Creating session for user: {}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ChatSession session = new ChatSession();
            session.setUser(user);
            session.setMessages("[]");

            session = chatSessionRepository.save(session);
            logger.info("Session created successfully: {}", session.getId());
            return toResponse(session);
        } catch (Exception e) {
            logger.error("Error creating chat session", e);
            throw e;
        }
    }

    public ChatSessionResponse getById(UUID id) {
        try {
            UUID userId = getCurrentUserId();
            logger.info("Fetching session {} for user {}", id, userId);
            return chatSessionRepository.findById(id)
                    .filter(session -> session.getUser().getId().equals(userId))
                    .map(this::toResponse)
                    .orElseThrow(() -> new RuntimeException("Chat session not found or unauthorized"));
        } catch (Exception e) {
            logger.error("Error fetching session by id: {}", id, e);
            throw e;
        }
    }

    public SseEmitter sendMessage(UUID sessionId, String message) {
        try {
            UUID userId = getCurrentUserId();
            logger.info("Sending message in session {} for user {}", sessionId, userId);

            ChatSession session = chatSessionRepository.findById(sessionId)
                    .filter(s -> s.getUser().getId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("Chat session not found or unauthorized"));

            SseEmitter emitter = new SseEmitter(60_000L);

            executor.execute(() -> {
                try {
                    logger.debug("Processing AI response for session {}", sessionId);
                    List<ChatMessage> history = parseMessages(session.getMessages());

                    if (history.isEmpty()) {
                        history.add(new ChatMessage("system",
                                "You are a friendly and knowledgeable anime recommendation assistant. " +
                                        "Help users find anime they'll love based on their preferences, mood, " +
                                        "and past favorites. Be enthusiastic but concise. " +
                                        "When recommending anime, mention the title, genre, and a brief reason why they'd enjoy it."));
                    }

                    history.add(new ChatMessage("user", message));

                    String aiResponse = aiService.getChatResponse(history);

                    if (aiResponse == null || aiResponse.isBlank()) {
                        logger.warn("AI response was null or blank for session {}", sessionId);
                        aiResponse = "I'm sorry, I couldn't generate a response right now. Please try again.";
                    }

                    history.add(new ChatMessage("assistant", aiResponse));

                    String updatedJson = objectMapper.writeValueAsString(history);
                    session.setMessages(updatedJson);
                    chatSessionRepository.save(session);
                    logger.info("Successfully updated session {} with AI response", sessionId);

                    emitter.send(SseEmitter.event().data(aiResponse));
                    emitter.complete();

                } catch (Exception e) {
                    logger.error("Error within executor for session: {}", sessionId, e);
                    try {
                        emitter.send(
                                SseEmitter.event().data("I'm having trouble connecting right now. Please try again."));
                        emitter.complete();
                    } catch (IOException ioEx) {
                        logger.error("Failed to stream error message to client for session {}", sessionId, ioEx);
                        emitter.completeWithError(ioEx);
                    }
                }
            });

            return emitter;
        } catch (Exception e) {
            logger.error("Error initiating send message for session: {}", sessionId, e);
            throw e;
        }
    }

    private List<ChatMessage> parseMessages(String messagesJson) {
        try {
            return objectMapper.readValue(messagesJson, new TypeReference<List<ChatMessage>>() {
            });
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private ChatSessionResponse toResponse(ChatSession session) {
        return new ChatSessionResponse(
                session.getId(),
                session.getMessages(),
                session.getCreatedAt(),
                session.getUpdatedAt());
    }
}
