package com.example.demo.controller;

import jakarta.validation.Valid;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.security.RoleRepository;
import com.example.demo.security.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        // sprawdzanie czy ktos taki nie istnieje

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Błąd: Email jest już zajęty!");
        }

        // tworzenie obiektu uzytkownika
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);

        // hashowanie hasla przy pomocy security config

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        // pobranie i przypisanie domyslnej roli uzytkownikowi
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Błąd: Nie znaleziono roli ROLE_USER w bazie danych."));
        user.getRoles().add(userRole);

        // zapisanie uzytkownika
        userRepository.save(user);

        return ResponseEntity.ok("Użytkownik zarejestrowany pomyślnie!");
    }
}

// do odebrania danych z Jsona
class RegisterRequest {

    @NotBlank(message = "Email nie może być pusty")
    @Email(message = "Podano niepoprawny format email")
    private String email;

    @NotBlank(message = "Hasło nie może być puste")
    @Size(min = 6, message = "Hasło musi mieć minimum 6 znaków")
    private String password;

    @NotBlank(message = "Imię nie może być puste")
    private String firstName;

    @NotBlank(message = "Nazwisko nie może być puste")
    private String lastName;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}