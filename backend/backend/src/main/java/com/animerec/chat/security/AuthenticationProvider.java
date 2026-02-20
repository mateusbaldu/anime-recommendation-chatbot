package com.animerec.chat.security;

import com.animerec.chat.models.User;
import com.animerec.chat.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class AuthenticationProvider {

    private final UserRepository userRepository;

    public AuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID getCurrentUserId() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();

        // Guest user: principal is the User entity itself
        if (principal instanceof User guestUser) {
            return guestUser.getId();
        }

        // OAuth2 user: principal is OAuth2User
        if (principal instanceof OAuth2User oAuth2User) {
            String googleSub = Optional.ofNullable(oAuth2User.<String>getAttribute("sub"))
                    .orElseThrow(() -> new RuntimeException("Google 'sub' not found in OAuth2 token"));

            return userRepository.findByGoogleSub(googleSub)
                    .orElseGet(() -> createUserFromOAuth2(oAuth2User, googleSub))
                    .getId();
        }

        throw new RuntimeException("Unsupported authentication principal type");
    }

    public User getCurrentUser() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();

        // Guest user: return the User entity directly
        if (principal instanceof User guestUser) {
            return guestUser;
        }

        // OAuth2 user: look up by ID
        UUID userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public OAuth2User getCurrentOAuth2User() {
        return getAuthenticatedOAuth2User();
    }

    private Authentication getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
    }

    private OAuth2User getAuthenticatedOAuth2User() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(auth -> (OAuth2User) auth.getPrincipal())
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
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
}
