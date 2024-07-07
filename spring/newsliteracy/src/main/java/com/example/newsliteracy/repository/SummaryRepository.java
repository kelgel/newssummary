package com.example.newsliteracy.repository;

import com.example.newsliteracy.model.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {
    List<Summary> findByUserId(Long userId);
}
