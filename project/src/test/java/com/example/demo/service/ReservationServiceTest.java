package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Reservation;
import com.example.demo.model.ReservationStatus;
import com.example.demo.model.User;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ReservationService reservationService;

    @Test
    void reserveBook_success_decrementsStockAndSavesReservation() {
        User u = new User();
        u.setEmail("jan@example.com");
        Book b = new Book();
        b.setQuantity(1);
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(u));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        Reservation reservation = reservationService.reserveBook(1L, "jan@example.com");

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.ACTIVE);
        assertThat(reservation.getExpiresAt()).isNotNull();
        assertThat(b.getQuantity()).isEqualTo(0);
        verify(bookRepository).save(b);
    }

    @Test
    void reserveBook_userNotFound_throws() {
        when(userRepository.findByEmail("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.reserveBook(1L, "ghost"))
                .hasMessage("Nie znaleziono użytkownika");
    }

    @Test
    void reserveBook_bookNotFound_throws() {
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(new User()));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.reserveBook(1L, "jan@example.com"))
                .hasMessage("Nie znaleziono książki o podanym ID");
    }

    @Test
    void reserveBook_outOfStock_throws() {
        Book b = new Book();
        b.setQuantity(0);
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(new User()));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(b));

        assertThatThrownBy(() -> reservationService.reserveBook(1L, "jan@example.com"))
                .hasMessage("Brak wolnych egzemplarzy tej książki!");
    }
}
