package com.example.newsliteracy.controller;

import com.example.newsliteracy.model.Summary;
import com.example.newsliteracy.model.User;
import com.example.newsliteracy.service.NewsService;
import com.example.newsliteracy.service.OpenAIService;
import com.example.newsliteracy.service.SummaryService;
import com.example.newsliteracy.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private UserService userService;

    @Autowired
    private SummaryService summaryService;

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
            e.printStackTrace(); // 에러 메시지를 로그로 출력
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
            e.printStackTrace(); // 에러 메시지를 로그로 출력
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
            e.printStackTrace(); // 에러 메시지를 로그로 출력
        }
        return "article";
    }

    @PostMapping("/summarize")
    public String summarizeArticle(@RequestParam("article") String articleContent,
                                   @RequestParam("summary") String summaryContent,
                                   @RequestParam("url") String articleUrl,
                                   Model model) {
        try {
            // Get the current authenticated user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();
            User user = userService.findByUsername(username);

            // Generate the summary
            String generatedSummary = openAIService.generateSummary(articleContent);
            Map<String, Object> evaluation = openAIService.evaluateSummary(articleContent, summaryContent);

            // Save the summary and original article URL
            Summary summary = new Summary();
            summary.setOriginalArticle(articleContent);
            summary.setSubmittedSummary(summaryContent);
            summary.setOriginalUrl(articleUrl);
            summary.setUser(user);
            summaryService.saveSummary(summary);

            // Add attributes for the result page
            model.addAttribute("generatedSummary", generatedSummary);
            model.addAttribute("submittedSummary", summaryContent);
            model.addAttribute("scores", evaluation);
            model.addAttribute("accuracyExplanation", evaluation.get("AccuracyExplanation"));
            model.addAttribute("brevityExplanation", evaluation.get("BrevityExplanation"));
            model.addAttribute("clarityExplanation", evaluation.get("ClarityExplanation"));
            model.addAttribute("comprehensivenessExplanation", evaluation.get("ComprehensivenessExplanation"));
        } catch (Exception e) {
            model.addAttribute("error", "Failed to summarize or evaluate: " + e.getMessage());
            e.printStackTrace(); // 에러 메시지를 로그로 출력
        }
        return "result";
    }
}
