package com.littlebizsolutions.easyrota.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank String refreshToken) {}
