package com.animerec.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/users/**").authenticated()
                        .requestMatchers("/chats/**").authenticated()
                        .requestMatchers("/works", "/works/**").permitAll()
                        .requestMatchers("/api/import/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect(request.getContextPath() + "/auth/me");
                        }))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                        }));

        return http.build();
    }
}
