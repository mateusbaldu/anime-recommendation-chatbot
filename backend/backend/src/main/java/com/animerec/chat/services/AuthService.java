package com.animerec.chat.services;

import com.animerec.chat.dto.response.UserResponse;
import com.animerec.chat.security.AuthenticationProvider;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationProvider authenticationProvider;

    public AuthService(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public UserResponse getCurrentUser() {
        var user = authenticationProvider.getCurrentUser();
        
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getProfileImage(),
                user.getCreatedAt(),
                user.getConsentedAt()
        );
    }
}
