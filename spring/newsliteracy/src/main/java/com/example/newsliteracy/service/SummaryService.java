package com.example.newsliteracy.service;

import com.example.newsliteracy.model.Summary;
import com.example.newsliteracy.model.SummaryScoreHistory;
import com.example.newsliteracy.model.User;
import com.example.newsliteracy.repository.SummaryRepository;
import com.example.newsliteracy.repository.SummaryScoreHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SummaryService {

    @Autowired
    private SummaryRepository summaryRepository;

    @Autowired
    private SummaryScoreHistoryRepository summaryScoreHistoryRepository;

    @Transactional
    public void saveSummary(Summary summary) {
        summaryRepository.save(summary);

        SummaryScoreHistory scoreHistory = new SummaryScoreHistory();
        scoreHistory.setSummary(summary);
        scoreHistory.setAccuracy(summary.getAccuracy());
        scoreHistory.setBrevity(summary.getBrevity());
        scoreHistory.setClarity(summary.getClarity());
        scoreHistory.setComprehensiveness(summary.getComprehensiveness());
        scoreHistory.setTotalScore(summary.getTotalScore());

        summaryScoreHistoryRepository.save(scoreHistory);
    }

    public List<Summary> findByUser(User user) {
        return summaryRepository.findByUser(user);
    }

    public Summary findById(Long id) {
        return summaryRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteById(Long id) {
        summaryScoreHistoryRepository.deleteBySummaryId(id);
        summaryRepository.deleteById(id);
    }

    @Transactional
    public void deleteSummaryScoreHistoryBySummaryId(Long summaryId) {
        summaryScoreHistoryRepository.deleteBySummaryId(summaryId);
    }
}
