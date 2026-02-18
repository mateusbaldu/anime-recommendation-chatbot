package com.animerec.chat.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatSessionResponse(
        UUID id,
        String messages,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
