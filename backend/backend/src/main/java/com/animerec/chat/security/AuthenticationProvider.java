package com.animerec.chat.security;

import com.animerec.chat.models.User;
import com.animerec.chat.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AuthenticationProvider {

    private final UserRepository userRepository;

    public AuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal();
        String googleSub = oAuth2User.getAttribute("sub");
        
        if (googleSub == null) {
            throw new RuntimeException("Google 'sub' not found in OAuth2 token");
        }
        
        User user = userRepository.findByGoogleSub(googleSub)
                .orElseGet(() -> createUserFromOAuth2(oAuth2User, googleSub));
        
        return user.getId();
    }

    private User createUserFromOAuth2(OAuth2User oAuth2User, String googleSub) {
        User newUser = new User();
        newUser.setGoogleSub(googleSub);
        newUser.setEmail(oAuth2User.getAttribute("email"));
        newUser.setDisplayName(oAuth2User.getAttribute("name"));
        newUser.setProfileImage(oAuth2User.getAttribute("picture"));
        newUser.setCreatedAt(LocalDateTime.now());
        
        return userRepository.save(newUser);
    }

    public OAuth2User getCurrentOAuth2User() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        return (OAuth2User) auth.getPrincipal();
    }

    public User getCurrentUser() {
        UUID userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
