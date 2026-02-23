package com.animerec.chat.config;

import com.animerec.chat.security.GuestAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;

import java.util.List;

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
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ASYNC).permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/users/**").authenticated()
                        .requestMatchers("/chats", "/chats/**").authenticated()
                        .requestMatchers("/api/import/**").authenticated()
                        .requestMatchers("/api/test-ai").permitAll()
                        .requestMatchers("/works", "/works/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(guestAuthenticationFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration
                .setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "http://localhost:80"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
