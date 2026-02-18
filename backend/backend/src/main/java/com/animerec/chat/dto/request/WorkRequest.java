package com.animerec.chat.dto.request;

import com.animerec.chat.enums.WorkType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record WorkRequest(
        Integer malId,

        @NotBlank
        String title,

        String titleEnglish,
        String synopsis,
        WorkType mediaType,

        @DecimalMin("0.00") @DecimalMax("10.00")
        BigDecimal externalScore,

        Integer popularityCount,
        String[] genres,
        String[] themes,
        String sourceName
) {}
