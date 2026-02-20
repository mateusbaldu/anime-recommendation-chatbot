package com.animerec.chat.config;

import com.animerec.chat.security.GuestAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final GuestAuthenticationFilter guestAuthenticationFilter;

    public SecurityConfig(GuestAuthenticationFilter guestAuthenticationFilter) {
        this.guestAuthenticationFilter = guestAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/users/**").authenticated()
                        .requestMatchers("/chats/**").authenticated()
                        .requestMatchers("/api/import/**").authenticated()
                        .requestMatchers("/works", "/works/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect(request.getContextPath() + "/auth/me");
                        }))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                        }))
                .addFilterBefore(guestAuthenticationFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}
