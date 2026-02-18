package com.animerec.chat.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserReviewResponse(
        UUID id,
        UUID workId,
        String workTitle,
        String externalTitle,
        String sourceName,
        BigDecimal normalizedScore,
        String reviewText,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt
) {}
