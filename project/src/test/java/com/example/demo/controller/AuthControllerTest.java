package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.security.RoleRepository;
import com.example.demo.security.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    AuthController authController;

    private RegisterRequest request() {
        RegisterRequest r = new RegisterRequest();
        r.setEmail("jan@example.com");
        r.setPassword("secret1");
        r.setFirstName("Jan");
        r.setLastName("Kowalski");
        return r;
    }

    @Test
    void register_success_savesEncodedUserWithRole() {
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.empty());
        Role role = new Role();
        role.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret1")).thenReturn("hashed");

        ResponseEntity<String> response = authController.register(request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Użytkownik zarejestrowany pomyślnie!");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("hashed");
        assertThat(saved.getEmail()).isEqualTo("jan@example.com");
        assertThat(saved.getRoles()).contains(role);
    }

    @Test
    void register_emailTaken_returnsBadRequest() {
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = authController.register(request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Błąd: Email jest już zajęty!");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_roleMissing_throws() {
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authController.register(request()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ROLE_USER");
    }
}
