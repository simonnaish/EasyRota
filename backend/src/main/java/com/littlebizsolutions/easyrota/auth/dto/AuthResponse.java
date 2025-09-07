package com.littlebizsolutions.easyrota.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        java.util.List<String> roles
) {
}
