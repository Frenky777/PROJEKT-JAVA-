package com.example.demo.controllers;

import com.example.demo.model.Reservation;
import com.example.demo.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    ReservationService reservationService;
    @InjectMocks
    ReservationController reservationController;

    private final Principal principal = () -> "jan@example.com";

    @Test
    void reserveBook_success_returnsOk() {
        Reservation reservation = new Reservation();
        when(reservationService.reserveBook(1L, "jan@example.com")).thenReturn(reservation);

        ResponseEntity<?> response = reservationController.reserveBook(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(reservation);
    }

    @Test
    void reserveBook_error_returnsBadRequest() {
        when(reservationService.reserveBook(1L, "jan@example.com"))
                .thenThrow(new RuntimeException("Brak wolnych egzemplarzy tej książki!"));

        ResponseEntity<?> response = reservationController.reserveBook(1L, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Brak wolnych egzemplarzy tej książki!");
    }
}
