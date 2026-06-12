package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.LoanHistory;
import com.example.demo.model.LoanStatus;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanHistoryRepository;
import com.example.demo.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    LoanHistoryRepository loanHistoryRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    LoanService loanService;

    private User user() {
        User u = new User();
        u.setEmail("jan@example.com");
        return u;
    }

    private Book book(int quantity) {
        Book b = new Book();
        b.setQuantity(quantity);
        return b;
    }

    @Test
    void borrowBook_success_decrementsStockAndSavesLoan() {
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user()));
        Book b = book(5);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));
        when(loanHistoryRepository.save(any(LoanHistory.class))).thenAnswer(i -> i.getArgument(0));

        LoanHistory loan = loanService.borrowBook(1L, "jan@example.com");

        assertThat(loan.getStatus()).isEqualTo(LoanStatus.BORROWED);
        assertThat(loan.getDueAt()).isNotNull();
        assertThat(b.getQuantity()).isEqualTo(4);
        verify(bookRepository).save(b);
    }

    @Test
    void borrowBook_userNotFound_throws() {
        when(userRepository.findByEmail("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.borrowBook(1L, "ghost"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Nie znaleziono użytkownika");
    }

    @Test
    void borrowBook_bookNotFound_throws() {
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user()));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.borrowBook(1L, "jan@example.com"))
                .hasMessage("Nie znaleziono książki");
    }

    @Test
    void borrowBook_outOfStock_throws() {
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user()));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book(0)));

        assertThatThrownBy(() -> loanService.borrowBook(1L, "jan@example.com"))
                .hasMessage("Brak dostępnych sztuk na magazynie!");
    }

    @Test
    void returnBook_success_incrementsStockAndMarksReturned() {
        Book b = book(2);
        LoanHistory loan = new LoanHistory();
        loan.setBook(b);
        loan.setStatus(LoanStatus.BORROWED);
        when(loanHistoryRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(loanHistoryRepository.save(any(LoanHistory.class))).thenAnswer(i -> i.getArgument(0));

        LoanHistory result = loanService.returnBook(10L);

        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(result.getReturnedAt()).isNotNull();
        assertThat(b.getQuantity()).isEqualTo(3);
    }

    @Test
    void returnBook_alreadyReturned_throws() {
        LoanHistory loan = new LoanHistory();
        loan.setBook(book(1));
        loan.setStatus(LoanStatus.RETURNED);
        when(loanHistoryRepository.findById(10L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnBook(10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ta książka została już wcześniej zwrócona!");
    }

    @Test
    void returnBook_notFound_throws() {
        when(loanHistoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.returnBook(99L))
                .hasMessage("Nie znaleziono takiego wypożyczenia");
    }

    @Test
    void getUserHistory_returnsRepositoryResult() {
        LoanHistory loan = new LoanHistory();
        when(loanHistoryRepository.findByUserEmail("jan@example.com")).thenReturn(List.of(loan));

        List<LoanHistory> history = loanService.getUserHistory("jan@example.com");

        assertThat(history).containsExactly(loan);
    }
}
