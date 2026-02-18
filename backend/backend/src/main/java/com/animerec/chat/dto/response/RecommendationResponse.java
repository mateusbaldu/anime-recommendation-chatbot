package com.animerec.chat.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecommendationResponse(
        UUID id,
        WorkSummaryResponse work,
        String reason,
        Boolean diversityFlag,
        UUID chatSessionId,
        LocalDateTime createdAt
) {}
