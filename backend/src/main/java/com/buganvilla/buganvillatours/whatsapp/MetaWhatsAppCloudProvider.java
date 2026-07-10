package com.buganvilla.buganvillatours.whatsapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * WhatsApp Business Cloud API provider (Meta oficial).
 * Activar con: whatsapp.provider=meta
 * Requiere: WHATSAPP_ACCESS_TOKEN, WHATSAPP_PHONE_NUMBER_ID
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
        if (!isEnabled()) { return; }
        String cleanPhone = cleanPhone(phone);
        log.info("Meta WhatsApp: sending reservation_confirmation to ***{}", cleanPhone.substring(Math.max(0, cleanPhone.length() - 4)));
        // TODO: implement template message via Graph API
        // POST /{phone-number-id}/messages with type=template, template.name=reservation_confirmation
    }

    @Override
    public void sendPaymentConfirmation(String phone, String name, BigDecimal amount, String reservaId) {
        if (!isEnabled()) { return; }
        String cleanPhone = cleanPhone(phone);
        log.info("Meta WhatsApp: sending payment_confirmation to ***{}", cleanPhone.substring(Math.max(0, cleanPhone.length() - 4)));
        // TODO: implement template message via Graph API
    }

    @Override
    public void sendReservationCancellation(String phone, String name, String packageName) {
        if (!isEnabled()) { return; }
        String cleanPhone = cleanPhone(phone);
        log.info("Meta WhatsApp: sending reservation_cancellation to ***{}", cleanPhone.substring(Math.max(0, cleanPhone.length() - 4)));
        // TODO: implement template message via Graph API
    }

    @Override
    public boolean isEnabled() {
        return phoneNumberId != null && !phoneNumberId.isBlank()
                && accessToken != null && !accessToken.isBlank();
    }

    private String cleanPhone(String phone) {
        if (phone == null) return "";
        String clean = phone.replaceAll("[^0-9]", "");
        return clean.length() == 9 ? "51" + clean : clean;
    }
}
