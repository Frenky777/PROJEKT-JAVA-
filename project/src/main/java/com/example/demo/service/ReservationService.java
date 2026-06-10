package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Reservation;
import com.example.demo.model.ReservationStatus;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.security.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }
    //transakcje jak w sqlu czyli jak wywali apke albo cos sie dziwnego stanie to nie przejdzie to działa jak w sumie operacja atomowa
    @Transactional
    public Reservation reserveBook(Long bookId, String userEmail) {
        // szukanie uzytkownika
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        // szukanie ksiazki
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono książki o podanym ID"));

        // sprawdzanie czy jest na magazynie
        if (book.getQuantity() <= 0) {
            throw new RuntimeException("Brak wolnych egzemplarzy tej książki!");
        }

        // zabranie jednej sztuki z polki i zapisanie
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        // tworzenie i zapisywanie rezerwacji
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setExpiresAt(LocalDateTime.now().plusHours(24)); // Ważna 24h

        return reservationRepository.save(reservation);
    }
}