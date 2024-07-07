package com.example.newsliteracy.controller;

import com.example.newsliteracy.service.NewsService;
import com.example.newsliteracy.service.OpenAIService;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class NewsController {
    private final NewsService newsService;
    private final OpenAIService openAIService;

    public NewsController(NewsService newsService, OpenAIService openAIService) {
        this.newsService = newsService;
        this.openAIService = openAIService;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            JSONObject news = newsService.getNewsByKeyword("latest");
            model.addAttribute("news", news.toMap());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch news: " + e.getMessage());
        }
        return "index";
    }

    @GetMapping("/search")
    public String searchNews(@RequestParam String keyword, Model model) {
        try {
            JSONObject news = newsService.getNewsByKeyword(keyword);
            model.addAttribute("news", news.toMap());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch news: " + e.getMessage());
        }
        return "index";
    }

    @GetMapping("/article/{index}")
    public String viewArticle(@PathVariable int index, Model model) {
        try {
            JSONObject article = newsService.getArticle(index);
            if (article != null) {
                model.addAttribute("article", article.toMap());
            } else {
                model.addAttribute("error", "Article not found.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch article: " + e.getMessage());
        }
        return "article";
    }

    @PostMapping("/summarize")
    public String summarizeArticle(@RequestParam("article") String article, @RequestParam("summary") String summary, Model model) {
        try {
            String generatedSummary = openAIService.generateSummary(article);
            Map<String, Object> evaluation = openAIService.evaluateSummary(article, summary);
            model.addAttribute("generatedSummary", generatedSummary);
            model.addAttribute("submittedSummary", summary);  // 추가된 부분
            model.addAttribute("scores", evaluation);
            model.addAttribute("accuracyExplanation", evaluation.get("AccuracyExplanation"));
            model.addAttribute("brevityExplanation", evaluation.get("BrevityExplanation"));
            model.addAttribute("clarityExplanation", evaluation.get("ClarityExplanation"));
            model.addAttribute("comprehensivenessExplanation", evaluation.get("ComprehensivenessExplanation"));
        } catch (Exception e) {
            model.addAttribute("error", "Failed to summarize or evaluate: " + e.getMessage());
        }
        return "result";
    }
}
