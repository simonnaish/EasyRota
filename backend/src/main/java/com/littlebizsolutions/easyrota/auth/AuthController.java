package com.littlebizsolutions.easyrota.auth;


import com.littlebizsolutions.easyrota.auth.dto.RegistrationRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    public record LoginRequest(@Email String email, @NotBlank String password) {}
    public record TokenRequest(@NotBlank String refreshToken) {}

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


}
