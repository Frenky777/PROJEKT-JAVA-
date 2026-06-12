package com.example.demo.security;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    CustomUserDetailsService service;

    @Test
    void loadUserByUsername_success_mapsRolesToAuthorities() {
        Role role = new Role();
        role.setName("ROLE_USER");
        User user = new User();
        user.setEmail("jan@example.com");
        user.setPassword("hashed");
        user.setEnabled(true);
        user.setRoles(Set.of(role));
        when(userRepository.findByEmail("jan@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("jan@example.com");

        assertThat(details.getUsername()).isEqualTo("jan@example.com");
        assertThat(details.getPassword()).isEqualTo("hashed");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_notFound_throws() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ghost@example.com");
    }
}
