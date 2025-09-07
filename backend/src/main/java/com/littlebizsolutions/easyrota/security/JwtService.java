package com.littlebizsolutions.easyrota.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")          // Either a long random string OR Base64 value (see base64Secret)
    private String secret;

    @Value("${app.jwt.secret.base64:false}") // set true if 'secret' is Base64-encoded
    private boolean base64Secret;

    @Value("${app.jwt.access.ttl-min:15}")
    private long accessTtlMin;

    @Value("${app.jwt.issuer:easyrota}")
    private String issuer;

    @Value("${app.jwt.clock-skew-sec:30}")   // small tolerance for clock drift
    private long clockSkewSec;

    private SecretKey key;

    @PostConstruct
    void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes); // returns SecretKey
    }

    public String generateAccessToken(Long userId, String email, List<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTtlMin * 60);

        return Jwts.builder()
                .id(UUID.randomUUID().toString()) // jti
                .issuer(issuer)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(Map.of(
                        "uid", userId,
                        "roles", roles
                ))
                .signWith(key)  // 0.13.0: still valid with inferred HS alg
                .compact();
    }

    /** Parses and validates the token, returning Claims or throws JwtValidationException. */
    public Claims parse(String jwt) {
        return Jwts.parser()
                .verifyWith(key)   // now matches SecretKey overload
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }


    /** Application-level exception you can catch in your filter/controller advice. */
    public static class JwtValidationException extends RuntimeException {
        public JwtValidationException(String msg, Throwable cause) { super(msg, cause); }
    }
}
