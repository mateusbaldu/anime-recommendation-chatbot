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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final AuthenticationProvider authenticationProvider;
    private final AIService aiService;
    private final ObjectMapper objectMapper;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ChatSessionService(
            ChatSessionRepository chatSessionRepository,
            UserRepository userRepository,
            AuthenticationProvider authenticationProvider,
            AIService aiService,
            ObjectMapper objectMapper) {
        this.chatSessionRepository = chatSessionRepository;
        this.userRepository = userRepository;
        this.authenticationProvider = authenticationProvider;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    private UUID getCurrentUserId() {
        return authenticationProvider.getCurrentUserId();
    }

    public Page<ChatSessionResponse> getSessions(Pageable pageable) {
        UUID userId = getCurrentUserId();
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ChatSessionResponse createSession() {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatSession session = new ChatSession();
        session.setUser(user);
        session.setMessages("[]");

        session = chatSessionRepository.save(session);
        return toResponse(session);
    }

    public ChatSessionResponse getById(UUID id) {
        UUID userId = getCurrentUserId();
        return chatSessionRepository.findById(id)
                .filter(session -> session.getUser().getId().equals(userId))
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Chat session not found or unauthorized"));
    }

    public SseEmitter sendMessage(UUID sessionId, String message) {
        UUID userId = getCurrentUserId();

        ChatSession session = chatSessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Chat session not found or unauthorized"));

        SseEmitter emitter = new SseEmitter(60_000L);

        executor.execute(() -> {
            try {

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
                    aiResponse = "I'm sorry, I couldn't generate a response right now. Please try again.";
                }

                history.add(new ChatMessage("assistant", aiResponse));

                String updatedJson = objectMapper.writeValueAsString(history);
                session.setMessages(updatedJson);
                chatSessionRepository.save(session);

                emitter.send(SseEmitter.event().data(aiResponse));
                emitter.complete();

            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().data("I'm having trouble connecting right now. Please try again."));
                    emitter.complete();
                } catch (IOException ioEx) {
                    emitter.completeWithError(ioEx);
                }
            }
        });

        return emitter;
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
