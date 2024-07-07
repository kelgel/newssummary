package com.example.newsliteracy.service;

import com.example.newsliteracy.model.Summary;
import com.example.newsliteracy.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {

    @Autowired
    private SummaryRepository summaryRepository;

    public void saveSummary(Summary summary) {
        summaryRepository.save(summary);
    }

    public List<Summary> getSummariesByUserId(Long userId) {
        return summaryRepository.findByUserId(userId);
    }
}
