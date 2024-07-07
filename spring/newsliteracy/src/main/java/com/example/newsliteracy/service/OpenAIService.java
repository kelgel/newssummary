package com.example.newsliteracy.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class OpenAIService {
    private static final Logger logger = Logger.getLogger(OpenAIService.class.getName());

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public OpenAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateSummary(String article) {
        String url = "https://api.openai.com/v1/chat/completions";
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");
        request.put("messages", new Object[]{
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "You are a helpful assistant.");
                }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", "Summarize the following article: " + article);
                }}
        });

        return extractContent(callOpenAIAPI(url, request));
    }

    public Map<String, Object> evaluateSummary(String originalArticle, String userSummary) {
        String url = "https://api.openai.com/v1/chat/completions";
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-3.5-turbo");
        request.put("messages", new Object[]{
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "You are a helpful assistant.");
                }},
                new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", "Evaluate the following summary of an article: " + originalArticle + "\n\nSummary: " + userSummary +
                            "\n\nProvide scores and detailed explanations for Accuracy, Brevity, Clarity, and Comprehensiveness, each out of 25 points.");
                }}
        });

        return extractEvaluation(callOpenAIAPI(url, request));
    }

    private String callOpenAIAPI(String url, Map<String, Object> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        logger.info("OpenAI API Response: " + response.getBody());

        return response.getBody();
    }

    private String extractContent(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONArray choices = jsonObject.getJSONArray("choices");
        if (choices.length() > 0) {
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            return message.getString("content");
        }
        return "No content available";
    }

    private Map<String, Object> extractEvaluation(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONArray choices = jsonObject.getJSONArray("choices");
        Map<String, Object> evaluation = new HashMap<>();
        if (choices.length() > 0) {
            String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
            logger.info("Extracted Evaluation Content: " + content);
            evaluation = parseEvaluation(content);
        }
        return evaluation;
    }

    private Map<String, Object> parseEvaluation(String content) {
        Map<String, Object> evaluation = new HashMap<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().contains("accuracy")) {
                evaluation.put("Accuracy", extractScore(line));
                evaluation.put("AccuracyExplanation", line.substring(line.indexOf(":") + 1).trim());
            } else if (line.toLowerCase().contains("brevity")) {
                evaluation.put("Brevity", extractScore(line));
                evaluation.put("BrevityExplanation", line.substring(line.indexOf(":") + 1).trim());
            } else if (line.toLowerCase().contains("clarity")) {
                evaluation.put("Clarity", extractScore(line));
                evaluation.put("ClarityExplanation", line.substring(line.indexOf(":") + 1).trim());
            } else if (line.toLowerCase().contains("comprehensiveness")) {
                evaluation.put("Comprehensiveness", extractScore(line));
                evaluation.put("ComprehensivenessExplanation", line.substring(line.indexOf(":") + 1).trim());
            }
        }
        return evaluation;
    }

    private Integer extractScore(String line) {
        String[] parts = line.split(":");
        if (parts.length > 1) {
            String scorePart = parts[1].replaceAll("[^0-9]", "").trim();
            try {
                return Integer.parseInt(scorePart);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
