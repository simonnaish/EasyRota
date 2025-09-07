package com.littlebizsolutions.easyrota.auth.repositories;

import com.littlebizsolutions.easyrota.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // case-insensitive email match; DB is CITEXT so normal equality works
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
