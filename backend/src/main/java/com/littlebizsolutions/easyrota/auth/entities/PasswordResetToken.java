package com.littlebizsolutions.easyrota.auth.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PasswordResetToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="token_hash", nullable=false, unique=true, length=128)
    private String tokenHash;

    @Column(name="issued_at", nullable=false)
    private OffsetDateTime issuedAt = OffsetDateTime.now();

    @Column(name="expires_at", nullable=false)
    private OffsetDateTime expiresAt;

    @Column(nullable=false)
    private boolean used = false;

    private OffsetDateTime usedAt;

    private String userAgent;
    private String ipAddress;
}
