package com.example.newssummary.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatGPTService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ChatGPTService.class);

    public ChatGPTService() {
        this.restTemplate = new RestTemplate();
    }

    public String getSummary(String content) {
        if (content == null || content.isEmpty()) {
            return "Summary not available. The content is empty or missing.";
        }

        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // Use a concise and clear prompt for summarization
        String prompt = "Provide a concise summary of article\n" + content;

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");
        request.put("messages", List.of(message));
        request.put("max_tokens", 100);
        request.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody.containsKey("usage")) {
                Map<String, Object> usage = (Map<String, Object>) responseBody.get("usage");
                int totalTokens = (int) usage.get("total_tokens");
                int promptTokens = (int) usage.get("prompt_tokens");
                int completionTokens = (int) usage.get("completion_tokens");
                logger.info("Token usage: total_tokens = {}, prompt_tokens = {}, completion_tokens = {}", totalTokens, promptTokens, completionTokens);
            }
            if (responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");
                    if (messageResponse != null) {
                        return (String) messageResponse.get("content");
                    }
                }
            }
        }
        return "Summary not available.";
    }
}
