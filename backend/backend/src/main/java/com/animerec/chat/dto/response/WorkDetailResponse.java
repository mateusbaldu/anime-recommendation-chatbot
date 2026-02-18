package com.animerec.chat.dto.response;

import com.animerec.chat.enums.WorkType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkDetailResponse(
        UUID id,
        Integer malId,
        String title,
        String titleEnglish,
        String synopsis,
        WorkType mediaType,
        BigDecimal externalScore,
        Integer popularityCount,
        String[] genres,
        String[] themes,
        LocalDateTime createdAt
) {}
