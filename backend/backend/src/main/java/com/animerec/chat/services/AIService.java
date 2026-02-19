package com.animerec.chat.services;

import com.animerec.chat.dto.AIDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final String aiServiceUrl;

    public AIService(@Value("${ai.service.url:http://localhost:8000}") String aiServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.aiServiceUrl = aiServiceUrl;
    }

    public float[] getEmbedding(String text) {
        String url = aiServiceUrl + "/embed";
        AIDtos.EmbedRequest request = new AIDtos.EmbedRequest(text);

        try {
            AIDtos.EmbedResponse response = restTemplate.postForObject(url, request, AIDtos.EmbedResponse.class);
            if (response != null && response.getEmbedding() != null) {
                List<Float> embeddingList = response.getEmbedding();
                float[] embeddingArray = new float[embeddingList.size()];
                for (int i = 0; i < embeddingList.size(); i++) {
                    embeddingArray[i] = embeddingList.get(i);
                }
                return embeddingArray;
            }
        } catch (Exception e) {
            System.err.println("Error calling AI Service /embed: " + e.getMessage());
        }
        return new float[0];
    }

    public String getChatResponse(List<AIDtos.ChatMessage> history) {
        String url = aiServiceUrl + "/chat";
        AIDtos.ChatRequest request = new AIDtos.ChatRequest(history);

        try {
            AIDtos.ChatResponse response = restTemplate.postForObject(url, request, AIDtos.ChatResponse.class);
            if (response != null) {
                return response.getResponse();
            }
        } catch (Exception e) {
            System.err.println("Error calling AI Service /chat: " + e.getMessage());
            return "I'm sorry, I'm having trouble connecting to my brain right now.";
        }
        return null;
    }
}
