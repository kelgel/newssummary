package com.example.newssummary.model;

import java.util.List;

public class Response {
    private String status;
    private int totalResults;
    private List<Article> articles;

    // 기본 생성자
    public Response() {
    }

    // 모든 필드를 매개변수로 받는 생성자
    public Response(String status, int totalResults, List<Article> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    // Getter와 Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
