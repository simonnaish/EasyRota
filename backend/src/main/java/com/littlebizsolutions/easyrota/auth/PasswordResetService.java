// src/main/java/com/littlebizsolutions/easyrota/auth/PasswordResetService.java
package com.littlebizsolutions.easyrota.auth;

import com.littlebizsolutions.easyrota.auth.entities.PasswordResetToken;
import com.littlebizsolutions.easyrota.auth.entities.User;
import com.littlebizsolutions.easyrota.auth.repositories.PasswordResetTokenRepository;
import com.littlebizsolutions.easyrota.auth.repositories.UserRepository;
import com.littlebizsolutions.easyrota.util.Hashing;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender; // JavaMailSender implements MailSender

    @Value("${app.reset.ttl-min:30}")
    private long ttlMinutes;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.reset.path:/auth/reset-password}")
    private String resetPath;

    private static final SecureRandom RNG = new SecureRandom();

    private static String newOpaqueToken() {
        byte[] bytes = new byte[48]; // 384 bits
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Always return successfully to avoid user enumeration.
     */
    @Transactional
    public void requestReset(String email, String userAgent, String ip) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Do not reveal if email exists
            return;
        }
        var user = userOpt.get();

        var plain = newOpaqueToken();
        var hash = Hashing.sha256Hex(plain);

        var prt = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hash)
                .issuedAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusMinutes(ttlMinutes))
                .userAgent(userAgent)
                .ipAddress(ip)
                .build();
        tokenRepo.save(prt);

        String link = frontendUrl + resetPath + "?token=" + plain;
        sendEmail(user.getEmail(), link);
    }

    @Transactional
    public void confirmReset(String tokenPlain, String newPassword) {
        var hash = Hashing.sha256Hex(tokenPlain);
        var token = tokenRepo.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (token.isUsed() || token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Invalid token");
        }

        var user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        token.setUsed(true);
        token.setUsedAt(OffsetDateTime.now());
        // JPA dirty checking will persist both
    }

    private void sendEmail(String to, String resetLink) {
        try {
            var msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject("Reset your EasyRota password");
            msg.setText("""
                You requested a password reset.

                Click the link below to set a new password (valid for a limited time):
                %s

                If you didn't request this, you can ignore this email.
                """.formatted(resetLink));
            mailSender.send(msg);
        } catch (Exception e) {
            // In dev you might not have SMTP; log and continue
            System.out.println("Password reset email (dev): " + resetLink);
        }
    }
}
