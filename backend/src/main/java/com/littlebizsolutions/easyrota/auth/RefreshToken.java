// src/main/java/.../auth/RefreshToken.java
package com.littlebizsolutions.easyrota.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="token_hash", nullable=false, unique=true, length=128)
    private String tokenHash;

    @Column(name="issued_at", nullable=false)
    private OffsetDateTime issuedAt = OffsetDateTime.now();

    @Column(name="expires_at", nullable=false)
    private OffsetDateTime expiresAt;

    @Column(nullable=false)
    private boolean revoked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="replaced_by_id")
    private RefreshToken replacedBy;

    private String userAgent;
    private String ipAddress;
}
