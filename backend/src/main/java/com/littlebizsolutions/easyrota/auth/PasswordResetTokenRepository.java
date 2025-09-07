// src/main/java/com/littlebizsolutions/easyrota/auth/PasswordResetTokenRepository.java
package com.littlebizsolutions.easyrota.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
