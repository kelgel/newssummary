package com.example.newsliteracy.controller;

import com.example.newsliteracy.model.Summary;
import com.example.newsliteracy.model.User;
import com.example.newsliteracy.service.SummaryService;
import com.example.newsliteracy.service.UserService;
import com.example.newsliteracy.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class SummaryController {

    private static final Logger logger = Logger.getLogger(SummaryController.class.getName());

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private OpenAIService openAIService;

    @PostMapping("/submitSummary")
    public String submitSummary(@RequestParam("article") String articleContent,
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

            // Calculate total score
            int totalScore = (int) evaluation.get("Accuracy") + (int) evaluation.get("Brevity") + (int) evaluation.get("Clarity") + (int) evaluation.get("Comprehensiveness");

            // Save the summary and original article URL
            Summary summary = new Summary();
            summary.setOriginalArticle(articleContent);
            summary.setSubmittedSummary(summaryContent);
            summary.setOriginalUrl(articleUrl);
            summary.setUser(user);
            summary.setAccuracy((int) evaluation.get("Accuracy"));
            summary.setBrevity((int) evaluation.get("Brevity"));
            summary.setClarity((int) evaluation.get("Clarity"));
            summary.setComprehensiveness((int) evaluation.get("Comprehensiveness"));
            summary.setTotalScore(totalScore);
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
        try {
            Summary summary = summaryService.findById(id);
            if (summary != null) {
                summary.setSubmittedSummary(submittedSummary);

                // Regenerate the summary using OpenAI service
                String generatedSummary = openAIService.generateSummary(summary.getOriginalArticle());
                Map<String, Object> evaluation = openAIService.evaluateSummary(summary.getOriginalArticle(), submittedSummary);

                // Calculate total score
                int totalScore = (int) evaluation.get("Accuracy") + (int) evaluation.get("Brevity") + (int) evaluation.get("Clarity") + (int) evaluation.get("Comprehensiveness");

                // Update the summary with new scores
                summary.setAccuracy((int) evaluation.get("Accuracy"));
                summary.setBrevity((int) evaluation.get("Brevity"));
                summary.setClarity((int) evaluation.get("Clarity"));
                summary.setComprehensiveness((int) evaluation.get("Comprehensiveness"));
                summary.setTotalScore(totalScore);
                summaryService.saveSummary(summary);

                // Add attributes for the result page
                model.addAttribute("generatedSummary", generatedSummary);
                model.addAttribute("submittedSummary", submittedSummary);
                model.addAttribute("scores", evaluation);
                model.addAttribute("accuracyExplanation", evaluation.get("AccuracyExplanation"));
                model.addAttribute("brevityExplanation", evaluation.get("BrevityExplanation"));
                model.addAttribute("clarityExplanation", evaluation.get("ClarityExplanation"));
                model.addAttribute("comprehensivenessExplanation", evaluation.get("ComprehensivenessExplanation"));

                return "result"; // Redirect to the result page after editing
            } else {
                model.addAttribute("error", "Summary not found");
                return "editSummary";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit or evaluate: " + e.getMessage());
            e.printStackTrace(); // 에러 메시지를 로그로 출력
            return "editSummary";
        }
    }


    @PostMapping("/summary/delete")
    public String deleteSummary(@RequestParam Long id, Principal principal, Model model) {
        try {
            Summary summary = summaryService.findById(id);
            if (summary != null) {
                Object userPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String username = userPrincipal instanceof UserDetails ? ((UserDetails) userPrincipal).getUsername() : userPrincipal.toString();
                User user = userService.findByUsername(username);
                if (summary.getUser().equals(user)) {
                    // Delete associated records in summary_score_history
                    summaryService.deleteSummaryScoreHistoryBySummaryId(id);
                    summaryService.deleteById(id);
                    logger.info("Deleted summary with ID: " + id);
                } else {
                    model.addAttribute("error", "You do not have permission to delete this summary.");
                    logger.warning("User does not have permission to delete summary with ID: " + id);
                }
            } else {
                model.addAttribute("error", "Summary not found.");
                logger.warning("Summary not found with ID: " + id);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete summary: " + e.getMessage());
            e.printStackTrace();
            logger.severe("Failed to delete summary with ID: " + id + ". Error: " + e.getMessage());
        }
        return "redirect:/summaries";
    }
}
