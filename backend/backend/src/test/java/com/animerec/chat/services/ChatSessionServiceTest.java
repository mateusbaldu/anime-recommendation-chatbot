package com.animerec.chat.services;

import com.animerec.chat.dto.request.ChatMessage;
import com.animerec.chat.dto.response.ChatSessionResponse;
import com.animerec.chat.models.ChatSession;
import com.animerec.chat.models.User;
import com.animerec.chat.repositories.ChatSessionRepository;
import com.animerec.chat.repositories.UserRepository;
import com.animerec.chat.security.AuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private AIService aiService;

    @InjectMocks
    private ChatSessionService chatSessionService;

    private User mockUser;
    private ChatSession mockSession;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();

        mockUser = new User();
        mockUser.setId(currentUserId);

        mockSession = new ChatSession();
        mockSession.setId(UUID.randomUUID());
        mockSession.setUser(mockUser);
        mockSession.setMessages("[]");
        mockSession.setCreatedAt(LocalDateTime.now());
        mockSession.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getSessions_ReturnsPagedResponses() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatSession> page = new PageImpl<>(List.of(mockSession));
        
        when(chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(currentUserId, pageable)).thenReturn(page);

        Page<ChatSessionResponse> responses = chatSessionService.getSessions(pageable);

        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertEquals(mockSession.getId(), responses.getContent().get(0).id());
    }

    @Test
    void createSession_CreatesAndReturnsSession() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(mockUser));
        when(chatSessionRepository.save(any(ChatSession.class))).thenAnswer(i -> {
            ChatSession s = i.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        ChatSessionResponse response = chatSessionService.createSession();

        assertNotNull(response);
        assertEquals("[]", response.messages());
        verify(chatSessionRepository).save(any(ChatSession.class));
    }

    @Test
    void getById_ValidSession_ReturnsSession() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        when(chatSessionRepository.findById(mockSession.getId())).thenReturn(Optional.of(mockSession));

        ChatSessionResponse response = chatSessionService.getById(mockSession.getId());

        assertNotNull(response);
        assertEquals(mockSession.getId(), response.id());
    }

    @Test
    void getById_SessionNotFoundOrUnauthorized_ThrowsException() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        when(chatSessionRepository.findById(mockSession.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chatSessionService.getById(mockSession.getId()));
    }

    @Test
    void sendMessage_ReturnsSseEmitter() {
        when(authenticationProvider.getCurrentUserId()).thenReturn(currentUserId);
        when(chatSessionRepository.findById(mockSession.getId())).thenReturn(Optional.of(mockSession));
        
        // This test only verifies that an SseEmitter is successfully returned. 
        // The actual sending happens in a separate thread.
        SseEmitter emitter = chatSessionService.sendMessage(mockSession.getId(), "Hello!");

        assertNotNull(emitter);
    }
}
