package com.backend.notificacion_service.whatsapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * WhatsApp Business Cloud API (Meta oficial).
 *
 * Para activar:
 *   WHATSAPP_ENABLED=true
 *   WHATSAPP_PROVIDER=meta
 *   WHATSAPP_PHONE_NUMBER_ID=<tu-phone-number-id>
 *   WHATSAPP_ACCESS_TOKEN=<tu-access-token>
 *
 * Templates que debes crear en Meta Business Manager:
 *   - reservation_confirmation  → parámetros: nombre, paquete, fecha, personas
 *   - payment_confirmation      → parámetros: nombre, monto, id_reserva
 *   - reservation_cancellation  → parámetros: nombre, paquete
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "whatsapp.provider", havingValue = "meta")
public class MetaWhatsAppCloudProvider implements WhatsAppNotificationProvider {

    @Value("${whatsapp.api-base-url:https://graph.facebook.com}")
    private String apiBaseUrl;

    @Value("${whatsapp.api-version:v25.0}")
    private String apiVersion;

    @Value("${whatsapp.phone-number-id:}")
    private String phoneNumberId;

    @Value("${whatsapp.business-account-id:}")
    private String businessAccountId;

    @Value("${whatsapp.access-token:}")
    private String accessToken;

    @Value("${whatsapp.default-language:es_ES}")
    private String defaultLanguage;

    private final RestClient restClient;

    public MetaWhatsAppCloudProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void sendReservationConfirmation(String phone, String name, String packageName, LocalDate date, int persons,
                                            String reservaId) {
        if (!isEnabled()) return;
        sendTemplateMessage(cleanPhone(phone), "reservation_confirmation", List.of(
                textParam(name),
                textParam(packageName),
                textParam(date.toString()),
                textParam(String.valueOf(persons)),
                textParam(reservaId)
        ));
    }

    @Override
    public void sendPaymentConfirmation(String phone, String name, BigDecimal amount, String reservaId) {
        if (!isEnabled()) return;
        // Usa el template oficial 'order_confirmed' (aprobado en Meta)
        // Parámetros: {{1}}=nombre, {{2}}=id_reserva, {{3}}=fecha_estimada
        sendTemplateMessage(cleanPhone(phone), "order_confirmed", "en_US", List.of(
                textParam(name),
                textParam("RES-" + reservaId),
                textParam("S/ " + amount.toPlainString())
        ));
    }

    @Override
    public void sendReservationCancellation(String phone, String name, String packageName) {
        if (!isEnabled()) return;
        sendTemplateMessage(cleanPhone(phone), "reservation_cancellation", List.of(
                textParam(name),
                textParam(packageName)
        ));
    }

    @Override
    public boolean isEnabled() {
        return accessToken != null && !accessToken.isBlank();
    }

    private void sendTemplateMessage(String to, String templateName, List<Map<String, String>> parameters) {
        sendTemplateMessage(to, templateName, defaultLanguage, parameters);
    }

    private void sendTemplateMessage(String to, String templateName, String language, List<Map<String, String>> parameters) {
        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", to,
                "type", "template",
                "template", Map.of(
                        "name", templateName,
                        "language", Map.of("code", language),
                        "components", List.of(
                                Map.of("type", "body", "parameters", parameters)
                        )
                )
        );

        String resolvedPhoneNumberId = resolvePhoneNumberId();
        String url = apiBaseUrl + "/" + apiVersion + "/" + resolvedPhoneNumberId + "/messages";
        String suffix = to.length() >= 4 ? "***" + to.substring(to.length() - 4) : "***";

        try {
            restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Meta WhatsApp: template '{}' enviado a {}", templateName, suffix);
        } catch (Exception e) {
            log.error("Meta WhatsApp: error enviando '{}' a {}: {}", templateName, suffix, e.getMessage());
        }
    }

    private synchronized String resolvePhoneNumberId() {
        if (phoneNumberId != null && !phoneNumberId.isBlank()) return phoneNumberId;

        String wabaId = businessAccountId;
        if (wabaId == null || wabaId.isBlank()) {
            Map<?, ?> accounts = graphGet("/me/assigned_whatsapp_business_accounts?fields=id,name");
            wabaId = firstId(accounts, "No se encontró una cuenta de WhatsApp Business asignada al token");
            businessAccountId = wabaId;
        }

        Map<?, ?> numbers = graphGet("/" + wabaId + "/phone_numbers?fields=id,display_phone_number,verified_name");
        phoneNumberId = firstId(numbers, "La cuenta de WhatsApp Business no tiene números disponibles");
        log.info("Meta WhatsApp: cuenta y número emisor resueltos correctamente");
        return phoneNumberId;
    }

    private Map<?, ?> graphGet(String path) {
        Map<?, ?> response = restClient.get()
                .uri(apiBaseUrl + "/" + apiVersion + path)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);
        if (response == null) throw new IllegalStateException("Meta devolvió una respuesta vacía");
        return response;
    }

    private String firstId(Map<?, ?> response, String errorMessage) {
        Object dataValue = response.get("data");
        if (!(dataValue instanceof List<?> data) || data.isEmpty() || !(data.get(0) instanceof Map<?, ?> first)) {
            throw new IllegalStateException(errorMessage);
        }
        Object id = first.get("id");
        if (id == null || id.toString().isBlank()) throw new IllegalStateException(errorMessage);
        return id.toString();
    }

    private Map<String, String> textParam(String value) {
        return Map.of("type", "text", "text", value);
    }

    private String cleanPhone(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^0-9]", "");
    }
}
