package com.example.demo.security;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //szukanie po email
    Optional<User> findByEmail(String email);
}