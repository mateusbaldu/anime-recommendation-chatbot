package com.animerec.chat.services;

import com.animerec.chat.dto.response.UserResponse;
import com.animerec.chat.models.User;
import com.animerec.chat.security.AuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationProvider authenticationProvider;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@example.com");
        mockUser.setDisplayName("Test User");
        mockUser.setProfileImage("https://example.com/image.png");
        mockUser.setCreatedAt(LocalDateTime.now());
        mockUser.setConsentedAt(LocalDateTime.now());
    }

    @Test
    void getCurrentUser_ReturnsUserResponse_WhenAuthenticated() {
        // Arrange
        when(authenticationProvider.getCurrentUser()).thenReturn(mockUser);

        // Act
        UserResponse response = authService.getCurrentUser();

        // Assert
        assertNotNull(response);
        assertEquals(mockUser.getId(), response.id());
        assertEquals(mockUser.getEmail(), response.email());
        assertEquals(mockUser.getDisplayName(), response.displayName());
        assertEquals(mockUser.getProfileImage(), response.profileImage());
        assertEquals(mockUser.getCreatedAt(), response.createdAt());
        assertEquals(mockUser.getConsentedAt(), response.consentedAt());
        
        verify(authenticationProvider, times(1)).getCurrentUser();
    }
}
