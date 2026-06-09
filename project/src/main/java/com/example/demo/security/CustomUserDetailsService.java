package com.example.demo.security;

import com.example.demo.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User myUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + email));


        List<SimpleGrantedAuthority> authorities = myUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                myUser.getEmail(),
                myUser.getPassword(),
                myUser.isEnabled(),
                true, true, true,
                authorities
        );
    }
}