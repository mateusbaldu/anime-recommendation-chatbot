package com.animerec.chat.services;

import com.animerec.chat.dto.AIDtos;
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
        AIDtos.EmbedRequest request = new AIDtos.EmbedRequest(text);

        return Optional.ofNullable(restTemplate.postForObject(url, request, AIDtos.EmbedResponse.class))
                .map(AIDtos.EmbedResponse::getEmbedding)
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
        throw new com.animerec.chat.exceptions.AiServiceUnavailableException("AI Service is having trouble right now.", e);
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @org.springframework.retry.annotation.Backoff(delay = 1000))
    public String getChatResponse(List<AIDtos.ChatMessage> history) {
        String url = aiServiceUrl + "/chat";
        AIDtos.ChatRequest request = new AIDtos.ChatRequest(history);

        return Optional.ofNullable(restTemplate.postForObject(url, request, AIDtos.ChatResponse.class))
                .map(AIDtos.ChatResponse::getResponse)
                .orElse(null);
    }

    @org.springframework.retry.annotation.Recover
    public String recoverGetChatResponse(Exception e, List<AIDtos.ChatMessage> history) {
        throw new com.animerec.chat.exceptions.AiServiceUnavailableException("I'm sorry, I'm having trouble connecting to my brain right now.", e);
    }
}
