package com.littlebizsolutions.easyrota.auth;

import com.littlebizsolutions.easyrota.auth.dto.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long register(RegistrationRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        var user = User.builder()
                .email(req.email().trim())
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .build();

        // default role for self-registered accounts (adjust to your flow)
        var roleUser = roleRepo.findByName("ROLE_OWNER")
                .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_OWNER").build()));
        user.getRoles().add(roleUser);

        var saved = userRepo.save(user);
        return saved.getId();
    }
}
