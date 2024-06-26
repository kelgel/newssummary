package com.example.newssummary.controller;

import com.example.newssummary.model.Article;
import com.example.newssummary.service.NewsService;
import com.example.newssummary.service.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private ChatGPTService chatGPTService;

    @GetMapping("/")
    public String index(Model model) {
        List<Article> articles = newsService.fetchArticles();
        model.addAttribute("articles", articles);
        return "index";
    }

    /*@GetMapping("/category")
    public String getByCategory(@RequestParam String category, Model model) {
        List<Article> articles = newsService.getNewsByCategory(category);
        model.addAttribute("articles", articles);
        return "index";
    }*/
    @GetMapping("/category/{category}")
    public String category(@PathVariable String category, Model model) {
        List<Article> articles = newsService.fetchArticlesByCategory(category);
        model.addAttribute("articles", articles);
        model.addAttribute("category", category);
        return "category";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Article> articles = newsService.searchArticles(query);
        model.addAttribute("articles", articles);
        return "search";
    }

    @GetMapping("/article/{id}")
    public String viewArticle(@PathVariable Long id, Model model) {
        Article article = newsService.getArticleById(id);
        if (article == null) {
            return "error"; // return an error page if article is not found
        }
        model.addAttribute("article", article);
        return "article";
    }

    @GetMapping("/article/{id}/summarize")
    public String summarizeArticle(@PathVariable Long id, Model model) {
        Article article = newsService.getArticleById(id);
        if (article == null) {
            return "error"; // return an error page if article is not found
        }
        String summary = chatGPTService.getSummary(article.getContent());
        model.addAttribute("summary", summary);
        model.addAttribute("article", article);
        return "article";
    }
}
