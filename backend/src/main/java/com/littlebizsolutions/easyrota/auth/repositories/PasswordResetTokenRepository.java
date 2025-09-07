// src/main/java/com/littlebizsolutions/easyrota/auth/PasswordResetTokenRepository.java
package com.littlebizsolutions.easyrota.auth.repositories;

import com.littlebizsolutions.easyrota.auth.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
