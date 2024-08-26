package com.example.newsliteracy.model;

import jakarta.persistence.*;

@Entity
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition="LONGTEXT")
    private String submittedSummary;

    @Column(nullable = false)
    private String originalArticle;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false)
    private int accuracy;

    @Column(nullable = false)
    private int brevity;

    @Column(nullable = false)
    private int clarity;

    @Column(nullable = false)
    private int comprehensiveness;

    @Column(nullable = false)
    private int totalScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubmittedSummary() {
        return submittedSummary;
    }

    public void setSubmittedSummary(String submittedSummary) {
        this.submittedSummary = submittedSummary;
    }

    public String getOriginalArticle() {
        return originalArticle;
    }

    public void setOriginalArticle(String originalArticle) {
        this.originalArticle = originalArticle;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getBrevity() {
        return brevity;
    }

    public void setBrevity(int brevity) {
        this.brevity = brevity;
    }

    public int getClarity() {
        return clarity;
    }

    public void setClarity(int clarity) {
        this.clarity = clarity;
    }

    public int getComprehensiveness() {
        return comprehensiveness;
    }

    public void setComprehensiveness(int comprehensiveness) {
        this.comprehensiveness = comprehensiveness;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
