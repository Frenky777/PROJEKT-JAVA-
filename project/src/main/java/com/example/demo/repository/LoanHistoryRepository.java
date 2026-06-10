package com.example.demo.repository;

import com.example.demo.model.LoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanHistoryRepository extends JpaRepository<LoanHistory, Long> {
    // spring wygeneruje zapytanie SQL wyciągające historię danego użytkownika
    List<LoanHistory> findByUserEmail(String email);
}