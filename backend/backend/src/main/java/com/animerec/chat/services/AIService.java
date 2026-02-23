package com.animerec.chat.services;

import com.animerec.chat.dto.request.ChatMessage;
import com.animerec.chat.dto.request.ChatRequest;
import com.animerec.chat.dto.request.EmbedRequest;
import com.animerec.chat.dto.response.ChatResponse;
import com.animerec.chat.dto.response.EmbedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final RestTemplate restTemplate;
    private final String aiServiceUrl;

    public AIService(@Value("${ai.service.url:http://localhost:8000}") String aiServiceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000); // 60 segundos
        factory.setReadTimeout(60000); // 60 segundos
        this.restTemplate = new RestTemplate(factory);
        this.aiServiceUrl = aiServiceUrl;
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @org.springframework.retry.annotation.Backoff(delay = 1000))
    public float[] getEmbedding(String text) {
        String url = aiServiceUrl + "/embed";
        EmbedRequest request = new EmbedRequest(text);

        logger.info("Iniciando chamada ao AI Service para /embed...");
        EmbedResponse response = restTemplate.postForObject(url, request, EmbedResponse.class);
        logger.info("Resposta recebida do AI Service para /embed");

        return Optional.ofNullable(response)
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

        logger.info("Iniciando chamada ao AI Service para /chat...");
        try {
            ChatResponse response = restTemplate.postForObject(url, request, ChatResponse.class);
            logger.info("Resposta recebida do AI Service para /chat");

            return Optional.ofNullable(response)
                    .map(ChatResponse::getResponse)
                    .orElse(null);
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            logger.error("Erro HTTP ao chamar o AI Service. Status: {} - Corpo: {}", e.getStatusCode(),
                    e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao chamar o AI Service: {}", e.getMessage(), e);
            throw e;
        }
    }

    @org.springframework.retry.annotation.Recover
    public String recoverGetChatResponse(Exception e, List<ChatMessage> history) {
        logger.error("Falha ao se comunicar com o AI Service após retentativas. Retornando fallback.");
        return "Desculpe, meu cérebro de IA está temporariamente indisponível.";
    }

    public boolean ping() {
        try {
            logger.info("Pinging AI Service at {}", aiServiceUrl);
            restTemplate.getForObject(aiServiceUrl + "/", String.class);
            return true;
        } catch (Exception e) {
            logger.error("Ping to AI Service failed: {}", e.getMessage());
            return false;
        }
    }
}
