package com.backend.notificacion_service.whatsapp;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface WhatsAppNotificationProvider {

    void sendReservationConfirmation(String phone, String name, String packageName, LocalDate date, int persons,
                                     String reservaId);

    void sendPaymentConfirmation(String phone, String name, BigDecimal amount, String reservaId);

    void sendReservationCancellation(String phone, String name, String packageName);

    boolean isEnabled();
}
