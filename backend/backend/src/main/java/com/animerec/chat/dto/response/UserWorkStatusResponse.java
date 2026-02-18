package com.animerec.chat.dto.response;

import com.animerec.chat.enums.WatchStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserWorkStatusResponse(
        UUID workId,
        String workTitle,
        WatchStatus status,
        LocalDateTime updatedAt
) {}
