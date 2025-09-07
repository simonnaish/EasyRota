package com.littlebizsolutions.easyrota.auth;

import com.littlebizsolutions.easyrota.auth.dto.RegistrationRequest;
import com.littlebizsolutions.easyrota.util.Hashing;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.littlebizsolutions.easyrota.security.JwtService;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshRepo;
    private final JwtService jwt;

    @Value("${app.jwt.refresh.ttl-days:14}")
    private long refreshTtlDays;

    private static final SecureRandom RNG = new SecureRandom();

    @Transactional
    public Long register(RegistrationRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        var user = User.builder()
                .email(req.email().trim())
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .build();

        // default role for self-registered accounts (adjust to your flow)
        var roleUser = roleRepo.findByName("ROLE_OWNER")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_OWNER").build()));
        user.getRoles().add(roleUser);

        var saved = userRepo.save(user);
        return saved.getId();
    }

    private static String newOpaqueToken() {
        var bytes = new byte[48]; // 384 bits
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public AuthResponse login(String email, String rawPassword, String userAgent, String ip) {
        var user = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        var roles = user.getRoles().stream().map(Role::getName).toList();

        // Create access token
        var access = jwt.generateAccessToken(user.getId(), user.getEmail(), roles);

        // Create refresh token (opaque, stored hashed)
        var refreshPlain = newOpaqueToken();
        var rt = RefreshToken.builder()
                .user(user)
                .tokenHash(Hashing.sha256Hex(refreshPlain))
                .issuedAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusDays(refreshTtlDays))
                .userAgent(userAgent)
                .ipAddress(ip)
                .build();
        refreshRepo.save(rt);

        return new AuthResponse(access, refreshPlain, user.getId(), user.getEmail(), roles);
    }

    @Transactional
    public AuthResponse refresh(String refreshPlain, String userAgent, String ip) {
        var hash = Hashing.sha256Hex(refreshPlain);
        var token = refreshRepo.findByTokenHash(hash).orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (token.isRevoked() || token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Invalid token");
        }
        var user = token.getUser();
        var roles = user.getRoles().stream().map(Role::getName).toList();

        // Rotate: revoke old, create new
        token.setRevoked(true);
        var newPlain = newOpaqueToken();
        var replacement = RefreshToken.builder()
                .user(user)
                .tokenHash(Hashing.sha256Hex(newPlain))
                .issuedAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusDays(refreshTtlDays))
                .userAgent(userAgent)
                .ipAddress(ip)
                .build();
        refreshRepo.save(replacement);
        token.setReplacedBy(replacement);

        var newAccess = jwt.generateAccessToken(user.getId(), user.getEmail(), roles);
        return new AuthResponse(newAccess, newPlain, user.getId(), user.getEmail(), roles);
    }

    @Transactional
    public void logout(String refreshPlain) {
        var hash = Hashing.sha256Hex(refreshPlain);
        refreshRepo.findByTokenHash(hash).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshRepo.save(rt);
        });
    }

    public record AuthResponse(
            String accessToken,
            String refreshToken,
            Long userId,
            String email,
            java.util.List<String> roles
    ) {
    }
}
