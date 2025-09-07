// src/main/java/.../auth/dto/RegistrationRequest.java
package com.littlebizsolutions.easyrota.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @Email(message = "{validation.email}")
        @NotBlank(message = "{validation.required}")
        String email,

        @NotBlank(message = "{validation.required}")
        @Size(min = 8, max = 72, message = "{validation.password.size}")
        String password,

        @Size(max = 120, message = "{validation.max}")
        String fullName
) {
}
