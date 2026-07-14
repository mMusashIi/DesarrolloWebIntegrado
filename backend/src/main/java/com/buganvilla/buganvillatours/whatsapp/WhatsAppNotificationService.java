package com.buganvilla.buganvillatours.whatsapp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppNotificationService {

    private final WhatsAppNotificationProvider provider;

    @Value("${whatsapp.enabled:false}")
    private boolean enabled;

    @Async
    public void notifyReservationConfirmation(String phone, String name, String packageName, LocalDate date, int persons) {
        if (!enabled) { return; }
        try {
            provider.sendReservationConfirmation(phone, name, packageName, date, persons);
        } catch (Exception e) {
            log.warn("WhatsApp notification failed (non-blocking): {}", e.getMessage());
        }
    }

    @Async
    public void notifyPaymentConfirmation(String phone, String name, BigDecimal amount, String reservaId) {
        if (!enabled) { return; }
        try {
            provider.sendPaymentConfirmation(phone, name, amount, reservaId);
        } catch (Exception e) {
            log.warn("WhatsApp notification failed (non-blocking): {}", e.getMessage());
        }
    }

    @Async
    public void notifyReservationCancellation(String phone, String name, String packageName) {
        if (!enabled) { return; }
        try {
            provider.sendReservationCancellation(phone, name, packageName);
        } catch (Exception e) {
            log.warn("WhatsApp notification failed (non-blocking): {}", e.getMessage());
        }
    }
}
