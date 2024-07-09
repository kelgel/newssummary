package com.example.newsliteracy.repository;

import com.example.newsliteracy.model.Summary;
import com.example.newsliteracy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    List<Summary> findByUser(User user);
}
