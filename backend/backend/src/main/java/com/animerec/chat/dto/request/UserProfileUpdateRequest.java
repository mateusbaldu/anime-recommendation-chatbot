package com.animerec.chat.dto.request;

import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
        @Size(max = 100)
        String displayName,

        @Size(max = 500)
        String profileImage
) {}
