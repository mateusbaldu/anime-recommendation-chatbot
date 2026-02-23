package com.animerec.chat.services;

import com.animerec.chat.dto.request.ChatMessage;
import com.animerec.chat.dto.request.ChatRequest;
import com.animerec.chat.dto.request.EmbedRequest;
import com.animerec.chat.dto.response.ChatResponse;
import com.animerec.chat.dto.response.EmbedResponse;
import com.animerec.chat.exceptions.AiServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AIService aiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiService, "aiServiceUrl", "http://localhost:8000");
        ReflectionTestUtils.setField(aiService, "restTemplate", restTemplate);
    }

    @Test
    void getEmbedding_Success() {
        EmbedResponse mockResponse = new EmbedResponse();
        mockResponse.setEmbedding(List.of(0.1f, 0.2f, 0.3f));

        when(restTemplate.postForObject(eq("http://localhost:8000/embed"), any(EmbedRequest.class),
                eq(EmbedResponse.class)))
                .thenReturn(mockResponse);

        float[] result = aiService.getEmbedding("Test text");

        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(0.1f, result[0]);
    }

    @Test
    void getChatResponse_Success() {
        ChatResponse mockResponse = new ChatResponse();
        mockResponse.setResponse("Hello from AI");

        when(restTemplate.postForObject(eq("http://localhost:8000/chat"), any(ChatRequest.class),
                eq(ChatResponse.class)))
                .thenReturn(mockResponse);

        String result = aiService.getChatResponse(List.of(new ChatMessage("user", "Hello")));

        assertEquals("Hello from AI", result);
    }

    @Test
    void recoverGetEmbedding_ThrowsException() {
        assertThrows(AiServiceUnavailableException.class,
                () -> aiService.recoverGetEmbedding(new RuntimeException("Test Error"), "text"));
    }

    @Test
    void recoverGetChatResponse_ReturnsFallback() {
        String result = aiService.recoverGetChatResponse(new RuntimeException("Test Error"), List.of());
        assertEquals("Desculpe, meu cérebro de IA está temporariamente indisponível.", result);
    }
}
