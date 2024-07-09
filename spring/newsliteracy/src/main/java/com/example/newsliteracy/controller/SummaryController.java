package com.example.newsliteracy.controller;

import com.example.newsliteracy.model.Summary;
import com.example.newsliteracy.model.User;
import com.example.newsliteracy.service.OpenAIService;
import com.example.newsliteracy.service.SummaryService;
import com.example.newsliteracy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private OpenAIService openAIService;

    @PostMapping("/submitSummary")
    public String submitSummary(
            @RequestParam("originalArticle") String originalArticle,
            @RequestParam("submittedSummary") String submittedSummary,
            @RequestParam("originalUrl") String originalUrl,
            Principal principal,
            Model model) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        Summary summary = new Summary();
        summary.setOriginalArticle(originalArticle);
        summary.setSubmittedSummary(submittedSummary);
        summary.setOriginalUrl(originalUrl);
        summary.setUser(user);
        summaryService.saveSummary(summary);

        // Generate and evaluate summary
        String generatedSummary = openAIService.generateSummary(originalArticle);
        Map<String, Object> evaluation = openAIService.evaluateSummary(originalArticle, submittedSummary);

        model.addAttribute("generatedSummary", generatedSummary);
        model.addAttribute("submittedSummary", submittedSummary);
        model.addAttribute("scores", evaluation);
        model.addAttribute("accuracyExplanation", evaluation.get("AccuracyExplanation"));
        model.addAttribute("brevityExplanation", evaluation.get("BrevityExplanation"));
        model.addAttribute("clarityExplanation", evaluation.get("ClarityExplanation"));
        model.addAttribute("comprehensivenessExplanation", evaluation.get("ComprehensivenessExplanation"));
        return "result";
    }

    @GetMapping("/summaries")
    public String viewSummaries(Principal principal, Model model) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<Summary> summaries = summaryService.findByUser(user);
        model.addAttribute("summaries", summaries);
        return "summaries";
    }

    @GetMapping("/summary/edit")
    public String editSummaryForm(@RequestParam Long id, Model model) {
        Summary summary = summaryService.findById(id);
        if (summary != null) {
            model.addAttribute("summary", summary);
            return "editSummary";
        } else {
            model.addAttribute("error", "Summary not found");
            return "redirect:/summaries";
        }
    }

    @PostMapping("/summary/edit")
    public String updateSummary(@RequestParam Long id,
                                @RequestParam String submittedSummary,
                                Model model) {
        Summary summary = summaryService.findById(id);
        if (summary != null) {
            summary.setSubmittedSummary(submittedSummary);
            summaryService.saveSummary(summary);

            // Re-evaluate the edited summary
            String originalArticle = summary.getOriginalArticle(); // Correct method name
            String generatedSummary = openAIService.generateSummary(originalArticle);
            Map<String, Object> evaluation = openAIService.evaluateSummary(originalArticle, submittedSummary);

            model.addAttribute("generatedSummary", generatedSummary);
            model.addAttribute("submittedSummary", submittedSummary);
            model.addAttribute("scores", evaluation);
            model.addAttribute("accuracyExplanation", evaluation.get("AccuracyExplanation"));
            model.addAttribute("brevityExplanation", evaluation.get("BrevityExplanation"));
            model.addAttribute("clarityExplanation", evaluation.get("ClarityExplanation"));
            model.addAttribute("comprehensivenessExplanation", evaluation.get("ComprehensivenessExplanation"));
            return "result";
        } else {
            model.addAttribute("error", "Summary not found");
            return "editSummary";
        }
    }

    @PostMapping("/summary/delete")
    public String deleteSummary(@RequestParam Long id) {
        summaryService.deleteById(id);
        return "redirect:/summaries";
    }
}
