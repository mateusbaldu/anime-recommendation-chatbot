package com.animerec.chat.services;

import com.animerec.chat.dto.request.ChatMessage;
import com.animerec.chat.dto.request.ChatRequest;
import com.animerec.chat.dto.request.EmbedRequest;
import com.animerec.chat.dto.response.ChatResponse;
import com.animerec.chat.dto.response.EmbedResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final String aiServiceUrl;

    public AIService(@Value("${ai.service.url:http://localhost:8000}") String aiServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.aiServiceUrl = aiServiceUrl;
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @org.springframework.retry.annotation.Backoff(delay = 1000))
    public float[] getEmbedding(String text) {
        String url = aiServiceUrl + "/embed";
        EmbedRequest request = new EmbedRequest(text);

        return Optional.ofNullable(restTemplate.postForObject(url, request, EmbedResponse.class))
                .map(EmbedResponse::getEmbedding)
                .map(this::toFloatArray)
                .orElse(new float[0]);
    }

    private float[] toFloatArray(List<Float> embeddingList) {
        float[] result = new float[embeddingList.size()];
        IntStream.range(0, embeddingList.size())
                .forEach(i -> result[i] = embeddingList.get(i));
        return result;
    }

    @org.springframework.retry.annotation.Recover
    public float[] recoverGetEmbedding(Exception e, String text) {
        throw new com.animerec.chat.exceptions.AiServiceUnavailableException("AI Service is having trouble right now.",
                e);
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @org.springframework.retry.annotation.Backoff(delay = 1000))
    public String getChatResponse(List<ChatMessage> history) {
        String url = aiServiceUrl + "/chat";
        ChatRequest request = new ChatRequest(history);

        return Optional.ofNullable(restTemplate.postForObject(url, request, ChatResponse.class))
                .map(ChatResponse::getResponse)
                .orElse(null);
    }

    @org.springframework.retry.annotation.Recover
    public String recoverGetChatResponse(Exception e, List<ChatMessage> history) {
        throw new com.animerec.chat.exceptions.AiServiceUnavailableException(
                "I'm sorry, I'm having trouble connecting to my brain right now.", e);
    }
}
