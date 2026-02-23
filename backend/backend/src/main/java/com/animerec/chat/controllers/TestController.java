package com.animerec.chat.controllers;

import com.animerec.chat.services.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    private final AIService aiService;

    public TestController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/test-ai")
    public ResponseEntity<String> testAiConnection() {
        boolean isConnected = aiService.ping();
        if (isConnected) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(500).body("Failed to connect to AI Service.");
        }
    }
}
