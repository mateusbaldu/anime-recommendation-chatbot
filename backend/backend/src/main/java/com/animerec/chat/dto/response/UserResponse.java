package com.animerec.chat.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        String profileImage,
        LocalDateTime createdAt,
        LocalDateTime consentedAt
) {}
