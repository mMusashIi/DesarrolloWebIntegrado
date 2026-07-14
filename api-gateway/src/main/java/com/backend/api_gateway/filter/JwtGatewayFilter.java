package com.backend.api_gateway.filter;

import com.backend.api_gateway.security.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGatewayFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, path, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtTokenUtil.validateToken(token)) {
            sendUnauthorized(response, path, "Invalid or expired JWT token");
            return;
        }

        log.debug("JWT valid for path: {}", path);
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        if (("POST".equalsIgnoreCase(method) && (matches(path, "/api/auth/login")
                || matches(path, "/api/auth/register")))
                || matches(path, "/api/mercadopago/webhook")
                || matches(path, "/api/mercadopago/pago-exitoso")
                || matches(path, "/api/mercadopago/pago-fallido")
                || matches(path, "/api/whatsapp/webhook")
                || matches(path, "/actuator/health")) {
            return true;
        }

        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }

        return matches(path, "/api/paquetes/**")
                || matches(path, "/api/paquetes")
                || matches(path, "/api/lugares/**")
                || matches(path, "/api/lugares")
                || matches(path, "/api/inventario/disponible")
                || matches(path, "/api/inventario/proximas-salidas")
                || matches(path, "/api/inventario/paquete/**");
    }

    private boolean matches(String path, String pattern) {
        return pathMatcher.match(pattern, path);
    }

    private void sendUnauthorized(HttpServletResponse response, String path, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, Object> body = new HashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", path);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
