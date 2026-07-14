package com.backend.notificacion_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String allowedOriginsRaw;

    @Value("${app.internal-token:dev-internal-token-change-me}")
    private String internalToken;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests(auth -> auth
                        // WhatsApp webhook verification (Meta calls this without auth)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/whatsapp/webhook").permitAll()
                        // Meta signs POST events with X-Hub-Signature-256; validation happens in the controller.
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/whatsapp/webhook").permitAll()
                        // DNI/RUC consumes a paid provider quota: authenticated users only.
                        .requestMatchers("/api/apis-net/**").authenticated()
                        // Internal endpoints for inter-service communication
                        .requestMatchers("/api/notificacion/**")
                        .access((authentication, context) ->
                                new AuthorizationDecision(hasValidInternalToken(
                                        context.getRequest().getHeader("X-Internal-Token"))))
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private boolean hasValidInternalToken(String providedToken) {
        return internalToken != null && !internalToken.isBlank() && providedToken != null
                && java.security.MessageDigest.isEqual(
                        internalToken.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                        providedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins = Arrays.stream(allowedOriginsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
