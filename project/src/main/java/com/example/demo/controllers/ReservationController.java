package com.example.demo.controllers;

import com.example.demo.model.Reservation;
import com.example.demo.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @PostMapping("/{bookId}")
    public ResponseEntity<?> reserveBook(@PathVariable Long bookId, Principal principal) {
        try {
            // principal.getName() zwraca email zalogowanego uzytkownika
            Reservation reservation = reservationService.reserveBook(bookId, principal.getName());
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            // jesli jakis  blad to zwroci nam blad
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}