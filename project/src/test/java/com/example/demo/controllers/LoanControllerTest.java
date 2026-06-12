package com.example.demo.controllers;

import com.example.demo.model.LoanHistory;
import com.example.demo.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    LoanService loanService;
    @InjectMocks
    LoanController loanController;

    private final Principal principal = () -> "jan@example.com";

    @Test
    void borrowBook_success_returnsOk() {
        LoanHistory loan = new LoanHistory();
        when(loanService.borrowBook(1L, "jan@example.com")).thenReturn(loan);

        ResponseEntity<?> response = loanController.borrowBook(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(loan);
    }

    @Test
    void borrowBook_error_returnsBadRequest() {
        when(loanService.borrowBook(1L, "jan@example.com"))
                .thenThrow(new RuntimeException("Brak dostępnych sztuk na magazynie!"));

        ResponseEntity<?> response = loanController.borrowBook(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Brak dostępnych sztuk na magazynie!");
    }

    @Test
    void returnBook_success_returnsOk() {
        LoanHistory loan = new LoanHistory();
        when(loanService.returnBook(10L)).thenReturn(loan);

        ResponseEntity<?> response = loanController.returnBook(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(loan);
    }

    @Test
    void returnBook_error_returnsBadRequest() {
        when(loanService.returnBook(10L))
                .thenThrow(new IllegalStateException("Ta książka została już wcześniej zwrócona!"));

        ResponseEntity<?> response = loanController.returnBook(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Ta książka została już wcześniej zwrócona!");
    }

    @Test
    void getMyHistory_returnsOkWithList() {
        LoanHistory loan = new LoanHistory();
        when(loanService.getUserHistory("jan@example.com")).thenReturn(List.of(loan));

        ResponseEntity<List<LoanHistory>> response = loanController.getMyHistory(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(loan);
    }
}
