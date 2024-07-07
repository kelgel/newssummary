package com.example.newsliteracy.controller;

import com.example.newsliteracy.model.Summary;
import com.example.newsliteracy.model.User;
import com.example.newsliteracy.service.SummaryService;
import com.example.newsliteracy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private UserService userService;

    @PostMapping("/submitSummary")
    public String submitSummary(
            @RequestParam("originalArticle") String originalArticle,
            @RequestParam("submittedSummary") String submittedSummary,
            Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        Summary summary = new Summary();
        summary.setOriginalArticle(originalArticle);
        summary.setSubmittedSummary(submittedSummary);
        summary.setUser(user);
        summaryService.saveSummary(summary);
        return "result"; // 리다이렉트 경로 확인
    }
}
