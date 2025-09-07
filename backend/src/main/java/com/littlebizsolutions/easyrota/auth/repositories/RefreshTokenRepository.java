// src/main/java/.../auth/RefreshTokenRepository.java
package com.littlebizsolutions.easyrota.auth.repositories;

import com.littlebizsolutions.easyrota.auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
