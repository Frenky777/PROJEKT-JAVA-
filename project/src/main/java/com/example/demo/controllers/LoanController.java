package com.example.demo.controllers;

import com.example.demo.model.LoanHistory;
import com.example.demo.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // wypożyczenie POST /api/loans/borrow/{bookId}
    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId, Principal principal) {
        try {
            LoanHistory loan = loanService.borrowBook(bookId, principal.getName());
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // zwrot POST /api/loans/return/{loanId}
    @PostMapping("/return/{loanId}")
    public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
        try {
            LoanHistory loan = loanService.returnBook(loanId);
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // historia GET /api/loans/my-history
    @GetMapping("/my-history")
    public ResponseEntity<List<LoanHistory>> getMyHistory(Principal principal) {
        return ResponseEntity.ok(loanService.getUserHistory(principal.getName()));
    }
}