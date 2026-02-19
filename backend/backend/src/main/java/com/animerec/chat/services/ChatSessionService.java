package com.animerec.chat.services;

import com.animerec.chat.dto.response.ChatSessionResponse;
import com.animerec.chat.models.ChatSession;
import com.animerec.chat.models.User;
import com.animerec.chat.repositories.ChatSessionRepository;
import com.animerec.chat.repositories.UserRepository;
import com.animerec.chat.security.AuthenticationProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final AuthenticationProvider authenticationProvider;

    public ChatSessionService(
            ChatSessionRepository chatSessionRepository,
            UserRepository userRepository,
            AuthenticationProvider authenticationProvider) {
        this.chatSessionRepository = chatSessionRepository;
        this.userRepository = userRepository;
        this.authenticationProvider = authenticationProvider;
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
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        return toResponse(session);
    }

    public SseEmitter sendMessage(UUID sessionId, String message) {
        throw new UnsupportedOperationException("AI service integration not implemented yet - requires FastAPI connection");
    }

    private ChatSessionResponse toResponse(ChatSession session) {
        return new ChatSessionResponse(
                session.getId(),
                session.getMessages(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
