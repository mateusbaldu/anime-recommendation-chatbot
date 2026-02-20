package com.animerec.chat.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EmbedResponse {
    private List<Float> embedding;
}
