package com.littlebizsolutions.easyrota.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(@NotBlank String token,
                                   @NotBlank @Size(min=8, max=72) String newPassword) {}

