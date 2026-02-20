package com.animerec.chat.security;

import com.animerec.chat.models.User;
import com.animerec.chat.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class GuestAuthenticationFilter extends OncePerRequestFilter {

    private static final String GUEST_SESSION_HEADER = "X-Guest-Session-Id";

    private final UserService userService;

    public GuestAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // If already authenticated (e.g. valid JWT), skip guest logic
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        String guestSessionId = request.getHeader(GUEST_SESSION_HEADER);

        if (guestSessionId != null && !guestSessionId.isBlank()) {
            User guestUser = userService.getOrCreateGuestUser(guestSessionId.trim());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    guestUser,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_GUEST")));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
