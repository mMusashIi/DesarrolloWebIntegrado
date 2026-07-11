package com.backend.pago_service.controller;

import com.backend.pago_service.model.dto.ResponseDTO;
import com.backend.pago_service.model.dto.payment.PreferenceRequestDTO;
import com.backend.pago_service.model.dto.payment.PreferenceResponseDTO;
import com.backend.pago_service.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

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

    @GetMapping("/pago-exitoso")
    public ResponseEntity<ResponseDTO<Map<String, String>>> pagoExitoso(
            @RequestParam(value = "collection_id", required = false) String collectionId,
            @RequestParam(value = "collection_status", required = false) String collectionStatus,
            @RequestParam(value = "payment_id", required = false) String paymentId,
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {

        return ResponseEntity.ok(ResponseDTO.success("Pago procesado correctamente", Map.of(
                "paymentId", paymentId != null ? paymentId : "",
                "status", collectionStatus != null ? collectionStatus : "",
                "reservaId", externalReference != null ? externalReference : "",
                "preferenceId", preferenceId != null ? preferenceId : ""
        )));
    }

    @GetMapping("/pago-fallido")
    public ResponseEntity<ResponseDTO<Map<String, String>>> pagoFallido(
            @RequestParam(value = "collection_status", required = false) String collectionStatus,
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {

        return ResponseEntity.ok(ResponseDTO.error(
                "El pago fue " + (collectionStatus != null ? collectionStatus : "fallido")));
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
