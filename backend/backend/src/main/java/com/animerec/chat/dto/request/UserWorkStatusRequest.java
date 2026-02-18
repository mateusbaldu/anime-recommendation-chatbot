package com.animerec.chat.dto.request;

import com.animerec.chat.enums.WatchStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserWorkStatusRequest(
        @NotNull
        UUID workId,

        @NotNull
        WatchStatus status
) {}
