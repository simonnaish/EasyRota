package com.littlebizsolutions.easyrota.auth;


import com.littlebizsolutions.easyrota.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService service;



    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest req) {
        Long userId = authService.register(req);
        return ResponseEntity.ok(new ApiResponse("registered", userId));
    }


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
