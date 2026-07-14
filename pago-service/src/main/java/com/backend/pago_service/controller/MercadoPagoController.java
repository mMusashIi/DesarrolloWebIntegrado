package com.backend.pago_service.controller;

import com.backend.pago_service.model.dto.ResponseDTO;
import com.backend.pago_service.model.dto.payment.PreferenceRequestDTO;
import com.backend.pago_service.model.dto.payment.PreferenceResponseDTO;
import com.backend.pago_service.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @PostMapping("/crear-preferencia")
    public ResponseEntity<ResponseDTO<PreferenceResponseDTO>> crearPreferencia(
            @RequestBody PreferenceRequestDTO request, Authentication authentication) {
        try {
            Long solicitanteId = Long.valueOf(authentication.getName());
            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            PreferenceResponseDTO response = mercadoPagoService.createPreference(
                    request.getReservaId(), solicitanteId, esAdmin);
            return ResponseEntity.ok(ResponseDTO.success("Preferencia creada exitosamente", response));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear preferencia MP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ResponseDTO.error("Error al crear la preferencia de pago: " + e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "topic", required = false) String topic,
            @RequestBody(required = false) Map<String, Object> body) {

        log.info("Webhook MP recibido — topic: {}, id: {}", topic, id);

        try {
            String paymentId = resolvePaymentId(id, topic, body);
            if (paymentId != null) {
                mercadoPagoService.procesarWebhook(paymentId);
            }
        } catch (Exception e) {
            log.error("Error procesando webhook MP: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Mercado Pago redirige aquí tras un pago exitoso.
     * Hacemos 302 al frontend Angular con los parámetros del pago.
     */
    @GetMapping("/pago-exitoso")
    public ResponseEntity<Void> pagoExitoso(
            @RequestParam(value = "collection_id", required = false) String collectionId,
            @RequestParam(value = "collection_status", required = false) String collectionStatus,
            @RequestParam(value = "payment_id", required = false) String paymentId,
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {

        log.info("MP pago-exitoso recibido — paymentId: {}, status: {}, reservaId: {}",
                paymentId, collectionStatus, externalReference);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/pago-exitoso")
                .queryParam("payment", "success")
                .queryParamIfPresent("payment_id", Optional.ofNullable(paymentId))
                .queryParamIfPresent("external_reference", Optional.ofNullable(externalReference))
                .queryParamIfPresent("collection_status", Optional.ofNullable(collectionStatus))
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * Mercado Pago redirige aquí tras un pago fallido o cancelado.
     * Hacemos 302 al frontend Angular.
     */
    @GetMapping("/pago-fallido")
    public ResponseEntity<Void> pagoFallido(
            @RequestParam(value = "collection_status", required = false) String collectionStatus,
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {

        log.warn("MP pago-fallido recibido — status: {}, reservaId: {}", collectionStatus, externalReference);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/pago-fallido")
                .queryParam("payment", "failure")
                .queryParamIfPresent("external_reference", Optional.ofNullable(externalReference))
                .queryParamIfPresent("collection_status", Optional.ofNullable(collectionStatus))
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @SuppressWarnings("unchecked")
    private String resolvePaymentId(String id, String topic, Map<String, Object> body) {
        if ("payment".equals(topic) && id != null && !id.isBlank()) return id;
        if (body != null && "payment".equals(body.get("type"))) {
            Object data = body.get("data");
            if (data instanceof Map) {
                Object dataId = ((Map<String, Object>) data).get("id");
                if (dataId != null) return String.valueOf(dataId);
            }
        }
        return null;
    }
}
