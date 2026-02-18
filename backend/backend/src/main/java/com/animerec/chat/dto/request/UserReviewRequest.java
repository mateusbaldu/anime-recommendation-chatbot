package com.animerec.chat.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserReviewRequest(
        UUID workId,

        String externalTitle,

        @NotBlank
        String sourceName,

        @DecimalMin("0.00") @DecimalMax("10.00")
        BigDecimal normalizedScore,

        String reviewText,
        LocalDateTime reviewedAt
) {}
