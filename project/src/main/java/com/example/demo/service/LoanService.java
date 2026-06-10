package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.LoanHistory;
import com.example.demo.model.LoanStatus;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.LoanHistoryRepository;
import com.example.demo.security.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {

    private final LoanHistoryRepository loanHistoryRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanHistoryRepository loanHistoryRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.loanHistoryRepository = loanHistoryRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // wypożyczanie ksiazki
    @Transactional
    public LoanHistory borrowBook(Long bookId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono książki"));

        if (book.getQuantity() <= 0) {
            throw new RuntimeException("Brak dostępnych sztuk na magazynie!");
        }

        // zmiana stanu magazynowego
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        LoanHistory loan = new LoanHistory();
        loan.setUser(user);
        loan.setBook(book);
        loan.setStatus(LoanStatus.BORROWED);
        loan.setDueAt(LocalDateTime.now().plusDays(14)); // Wypożyczenie na 14 dni

        return loanHistoryRepository.save(loan);
    }

    // obsluga zwrotu ksiazki
    @Transactional
    public LoanHistory returnBook(Long loanId) {
        LoanHistory loan = loanHistoryRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono takiego wypożyczenia"));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new RuntimeException("Ta książka została już wcześniej zwrócona!");
        }

        // zmiana statusu i wpisanie daty zwrotu
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(LocalDateTime.now());

        // zmiana stanu magazynowego dodajemy 1 do stanu
        Book book = loan.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookRepository.save(book);

        return loanHistoryRepository.save(loan);
    }

    // historia usera
    public List<LoanHistory> getUserHistory(String userEmail) {
        return loanHistoryRepository.findByUserEmail(userEmail);
    }
}