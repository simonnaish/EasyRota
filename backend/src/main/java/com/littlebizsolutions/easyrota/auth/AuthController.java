package com.littlebizsolutions.easyrota.auth;


import com.littlebizsolutions.easyrota.auth.dto.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService service;

    public record LoginRequest(@Email String email, @NotBlank String password) {}
    public record TokenRequest(@NotBlank String refreshToken) {}

    public record ForgotPasswordRequest(@Email @NotBlank String email) {}
    public record ResetPasswordRequest(@NotBlank String token,
                                       @NotBlank @Size(min=8, max=72) String newPassword) {}


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest req) {
        Long userId = authService.register(req);
        return ResponseEntity.ok(new ApiResponse("registered", userId));
    }

    record ApiResponse(String status, Long userId) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest http) {
        var ua = http.getHeader("User-Agent");
        var ip = http.getRemoteAddr();
        var res = authService.login(req.email(), req.password(), ua, ip);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRequest req, HttpServletRequest http) {
        var ua = http.getHeader("User-Agent");
        var ip = http.getRemoteAddr();
        var res = authService.refresh(req.refreshToken(), ua, ip);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody TokenRequest req) {
        authService.logout(req.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestAttribute(name="uid") Long uid,
                                @RequestAttribute(name="email") String email,
                                @RequestAttribute(name="roles") java.util.List<String> roles) {
        return ResponseEntity.ok(new java.util.HashMap<>() {{
            put("userId", uid);
            put("email", email);
            put("roles", roles);
        }});
    }




    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@RequestBody ForgotPasswordRequest req, HttpServletRequest http) {
        service.requestReset(req.email(), http.getHeader("User-Agent"), http.getRemoteAddr());
        return ResponseEntity.ok().build(); // always 200
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody ResetPasswordRequest req) {
        service.confirmReset(req.token(), req.newPassword());
        return ResponseEntity.noContent().build();
    }


}
