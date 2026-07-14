package com.backend.reserva_service.util;

import com.backend.reserva_service.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final JwtTokenUtil jwtTokenUtil;

    public Optional<Long> getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        try {
            return Optional.of(Long.parseLong(auth.getName()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getFullNameFromToken(String token) {
        return jwtTokenUtil.extractFullName(token);
    }
}
