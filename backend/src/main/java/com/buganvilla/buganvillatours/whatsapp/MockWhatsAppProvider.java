package com.buganvilla.buganvillatours.whatsapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@ConditionalOnProperty(name = "whatsapp.provider", havingValue = "mock", matchIfMissing = true)
public class MockWhatsAppProvider implements WhatsAppNotificationProvider {

    @Override
    public void sendReservationConfirmation(String phone, String name, String packageName, LocalDate date, int persons) {
        String phoneHash = phone != null ? phone.substring(Math.max(0, phone.length() - 4)) : "****";
        log.info("MOCK WhatsApp: would send reservation_confirmation to ***{} — {} persons for {} on {}",
                phoneHash, persons, packageName, date);
    }

    @Override
    public void sendPaymentConfirmation(String phone, String name, BigDecimal amount, String reservaId) {
        String phoneHash = phone != null ? phone.substring(Math.max(0, phone.length() - 4)) : "****";
        log.info("MOCK WhatsApp: would send payment_confirmation to ***{} — reserva #{} amount S/ {}",
                phoneHash, reservaId, amount);
    }

    @Override
    public void sendReservationCancellation(String phone, String name, String packageName) {
        String phoneHash = phone != null ? phone.substring(Math.max(0, phone.length() - 4)) : "****";
        log.info("MOCK WhatsApp: would send reservation_cancellation to ***{} — package: {}",
                phoneHash, packageName);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
