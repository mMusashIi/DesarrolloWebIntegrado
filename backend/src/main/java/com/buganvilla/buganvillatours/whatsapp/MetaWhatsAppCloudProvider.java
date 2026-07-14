package com.buganvilla.buganvillatours.whatsapp;

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

    @Value("${whatsapp.api-version:v18.0}")
    private String apiVersion;

    @Value("${whatsapp.phone-number-id:}")
    private String phoneNumberId;

    @Value("${whatsapp.access-token:}")
    private String accessToken;

    @Value("${whatsapp.default-language:es_ES}")
    private String defaultLanguage;

    private final RestClient restClient;

    public MetaWhatsAppCloudProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void sendReservationConfirmation(String phone, String name, String packageName, LocalDate date, int persons) {
        if (!isEnabled()) return;
        sendTemplateMessage(cleanPhone(phone), "reservation_confirmation", List.of(
                textParam(name),
                textParam(packageName),
                textParam(date.toString()),
                textParam(String.valueOf(persons))
        ));
    }

    @Override
    public void sendPaymentConfirmation(String phone, String name, BigDecimal amount, String reservaId) {
        if (!isEnabled()) return;
        sendTemplateMessage(cleanPhone(phone), "payment_confirmation", List.of(
                textParam(name),
                textParam("S/ " + amount.toPlainString()),
                textParam(reservaId)
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
        return phoneNumberId != null && !phoneNumberId.isBlank()
                && accessToken != null && !accessToken.isBlank();
    }

    private void sendTemplateMessage(String to, String templateName, List<Map<String, String>> parameters) {
        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", to,
                "type", "template",
                "template", Map.of(
                        "name", templateName,
                        "language", Map.of("code", defaultLanguage),
                        "components", List.of(
                                Map.of("type", "body", "parameters", parameters)
                        )
                )
        );

        String url = apiBaseUrl + "/" + apiVersion + "/" + phoneNumberId + "/messages";
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

    private Map<String, String> textParam(String value) {
        return Map.of("type", "text", "text", value);
    }

    private String cleanPhone(String phone) {
        if (phone == null) return "";
        String clean = phone.replaceAll("[^0-9]", "");
        return clean.length() == 9 ? "51" + clean : clean;
    }
}
