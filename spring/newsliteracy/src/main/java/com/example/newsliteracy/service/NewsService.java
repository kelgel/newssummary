package com.example.newsliteracy.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class NewsService {
    @Value("${newsapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private JSONObject newsCache;

    @Autowired
    public NewsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JSONObject getNewsByKeyword(String keyword) {
        String url = "https://newsapi.org/v2/everything?q=" + keyword + "&apiKey=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);
        newsCache = new JSONObject(response);
        return newsCache;
    }

    public JSONObject getArticle(int index) {
        if (newsCache != null && newsCache.has("articles")) {
            return newsCache.getJSONArray("articles").getJSONObject(index);
        }
        return null;
    }
}
