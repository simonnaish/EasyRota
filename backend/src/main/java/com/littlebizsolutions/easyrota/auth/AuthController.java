package com.littlebizsolutions.easyrota.auth;


import com.littlebizsolutions.easyrota.auth.dto.RegistrationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest req) {
        Long userId = authService.register(req);
        return ResponseEntity.ok(new ApiResponse("registered", userId));
    }

    record ApiResponse(String status, Long userId) {}
}
