package com.animerec.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class AIDtos {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmbedRequest {
        private String text;
    }

    @Data
    @NoArgsConstructor
    public static class EmbedResponse {
        private List<Float> embedding;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessage {
        private String role;
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRequest {
        private List<ChatMessage> history;
    }

    @Data
    @NoArgsConstructor
    public static class ChatResponse {
        private String response;
    }
}
