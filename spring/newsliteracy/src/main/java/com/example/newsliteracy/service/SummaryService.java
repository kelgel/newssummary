package com.example.newsliteracy.service;

import com.example.newsliteracy.model.Summary;
import com.example.newsliteracy.model.User;
import com.example.newsliteracy.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryService {
    @Autowired
    private SummaryRepository summaryRepository;

    public List<Summary> findByUser(User user) {
        return summaryRepository.findByUser(user);
    }

    public Summary findById(Long id) {
        return summaryRepository.findById(id).orElse(null);
    }

    public void saveSummary(Summary summary) {
        summaryRepository.save(summary);
    }

    public void deleteById(Long id) {
        summaryRepository.deleteById(id);
    }
}
