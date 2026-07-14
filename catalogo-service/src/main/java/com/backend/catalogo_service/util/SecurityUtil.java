package com.backend.catalogo_service.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtil {

    /**
     * Obtiene el email del usuario autenticado desde el SecurityContext.
     * El JWT fue emitido por auth-service; catalogo-service solo lo valida.
     */
    public Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.empty();
    }

    /**
     * No disponible en catalogo-service — la entidad Usuario reside en auth-service.
     */
    public Optional<Long> getCurrentUserId() {
        return Optional.empty();
    }

    /**
     * No disponible en catalogo-service — la entidad Usuario reside en auth-service.
     */
    public Optional<Object> getCurrentUser() {
        return Optional.empty();
    }
}
