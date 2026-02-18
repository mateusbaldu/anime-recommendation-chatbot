package com.animerec.chat.dto.response;

import com.animerec.chat.enums.WorkType;

import java.math.BigDecimal;
import java.util.UUID;

public record WorkSummaryResponse(
        UUID id,
        String title,
        String titleEnglish,
        WorkType mediaType,
        BigDecimal externalScore,
        Integer popularityCount,
        String[] genres
) {}
