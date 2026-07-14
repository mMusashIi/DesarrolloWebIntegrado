package com.buganvilla.buganvillatours.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

/**
 * Maneja el webhook de WhatsApp Business Cloud API (Meta).
 *
 * GET  /api/whatsapp/webhook  — verificación inicial del webhook por Meta
 * POST /api/whatsapp/webhook  — recepción de eventos (validados con HMAC-SHA256)
 *
 * Variables requeridas para activar:
 *   WHATSAPP_VERIFY_TOKEN  — token elegido libremente, se registra en Meta
 *   WHATSAPP_APP_SECRET    — app secret de la app Meta (para validar firma)
 */
@Slf4j
@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    @Value("${whatsapp.verify-token:}")
    private String verifyToken;

    @Value("${whatsapp.app-secret:}")
    private String appSecret;

    private final ObjectMapper objectMapper;

    public WhatsAppController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** Meta llama a este endpoint la primera vez para verificar el webhook */
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        if (verifyToken.isBlank()) {
            log.warn("WhatsApp webhook: WHATSAPP_VERIFY_TOKEN no configurado");
            return ResponseEntity.status(403).body("Token no configurado");
        }

        if ("subscribe".equals(mode) && token.equals(verifyToken)) {
            log.info("WhatsApp webhook: verificación exitosa");
            return ResponseEntity.ok(challenge);
        }

        log.warn("WhatsApp webhook: verificación fallida — token incorrecto o modo inválido");
        return ResponseEntity.status(403).body("Token inválido");
    }

    /** Meta envía eventos a este endpoint (status updates, mensajes entrantes, etc.) */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleEvent(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody byte[] rawBody) {

        if (!validateSignature(rawBody, signature)) {
            log.warn("WhatsApp webhook: firma HMAC inválida, evento rechazado");
            return ResponseEntity.status(403).body("Firma inválida");
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(rawBody, Map.class);
            String object = String.valueOf(event.get("object"));
            log.info("WhatsApp webhook: evento recibido, object={}", object);
        } catch (Exception e) {
            log.error("WhatsApp webhook: error procesando evento: {}", e.getMessage());
        }

        // Meta exige 200 en menos de 20 s independientemente del resultado interno
        return ResponseEntity.ok("EVENT_RECEIVED");
    }

    private boolean validateSignature(byte[] body, String signature) {
        if (appSecret == null || appSecret.isBlank()) {
            log.debug("WhatsApp webhook: WHATSAPP_APP_SECRET no configurado, omitiendo validación de firma");
            return true;
        }
        if (signature == null || !signature.startsWith("sha256=")) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = "sha256=" + HexFormat.of().formatHex(mac.doFinal(body));
            return MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8),
                    signature.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("WhatsApp webhook: error calculando HMAC: {}", e.getMessage());
            return false;
        }
    }
}
