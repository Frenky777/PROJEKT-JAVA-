package com.example.demo.controllers;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.security.RoleRepository;
import com.example.demo.security.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminUserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // sprawdzanie uzytkownikow w systemie
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // zmiana roli uzytkownika
    @PutMapping("/{userId}/role/{roleName}")
    public ResponseEntity<String> changeUserRole(@PathVariable Long userId, @PathVariable String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika."));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono takiej roli (dostępne: ROLE_USER, ROLE_ADMIN)."));

        // czyszczenie starej roli i nadanie nowej
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);

        return ResponseEntity.ok("Rola użytkownika " + user.getEmail() + " została zmieniona na " + roleName);
    }
}