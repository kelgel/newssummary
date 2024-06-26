package com.example.newssummary.service;

import com.example.newssummary.model.Response;
import com.example.newssummary.model.Article;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class NewsService {
    private static final String API_KEY = "f2c709ee882947a993ee698f07b2b866";
    private static final String URL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=" + API_KEY;
    private static final String NEWS_API_SEARCH_URL = "https://newsapi.org/v2/everything?q={query}&apiKey=" + API_KEY;
    private static final String NEWS_API_CATEGORY_URL = "https://newsapi.org/v2/top-headlines?country=us&category={category}&apiKey=" + API_KEY;
    @Autowired
    private RestTemplate restTemplate;

    private Map<Long, Article> articleRepository = new HashMap<>();
    private AtomicLong idGenerator = new AtomicLong();

    public List<Article> fetchArticles() {
        Response response = restTemplate.getForObject(URL, Response.class);
        if (response != null && response.getArticles() != null) {
            List<Article> articles = response.getArticles();
            for (Article article : articles) {
                long id = idGenerator.incrementAndGet();
                article.setId(id);
                articleRepository.put(id, article);
            }
            return articles;
        }
        return List.of(); // Return an empty list if no articles are found
    }


    /*public List<Article> getNews() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(URL, String.class);
        Gson gson = new Gson();
        Response newsResponse = gson.fromJson(response, Response.class);
        return newsResponse.getArticles();
    }*/
    /*public List<Article> getNewsByCategory(String category) {
        String url = URL + "&category=" + category;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        Gson gson = new Gson();
        Response newsResponse = gson.fromJson(response, Response.class);
        return newsResponse.getArticles();
    }*/
    public List<Article> fetchArticlesByCategory(String category) {
        Response response = restTemplate.getForObject(NEWS_API_CATEGORY_URL, Response.class, category);
        if (response != null && response.getArticles() != null) {
            List<Article> articles = response.getArticles();
            for (Article article : articles) {
                long id = idGenerator.incrementAndGet();
                article.setId(id);
                articleRepository.put(id, article);
            }
            return articles;
        }
        return List.of(); // Return an empty list if no articles are found
    }

    /*public List<Article> searchNews(String query) {
        String url = "https://newsapi.org/v2/everything?q=" + query + "&apiKey=" + API_KEY;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        Gson gson = new Gson();
        Response newsResponse = gson.fromJson(response, Response.class);
        return newsResponse.getArticles();
    }*/
    public List<Article> searchArticles(String query) {
        Response response = restTemplate.getForObject(NEWS_API_SEARCH_URL, Response.class, query);
        if (response != null && response.getArticles() != null) {
            List<Article> articles = response.getArticles();
            for (Article article : articles) {
                long id = idGenerator.incrementAndGet();
                article.setId(id);
                articleRepository.put(id, article);
            }
            return articles;
        }
        return List.of(); // Return an empty list if no articles are found
    }
    public Article getArticleById(Long id) {
        return articleRepository.get(id); // Fetch the article from the repository by ID
    }
}
