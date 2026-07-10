package com.buganvilla.buganvillatours.whatsapp;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface WhatsAppNotificationProvider {

    void sendReservationConfirmation(String phone, String name, String packageName, LocalDate date, int persons);

    void sendPaymentConfirmation(String phone, String name, BigDecimal amount, String reservaId);

    void sendReservationCancellation(String phone, String name, String packageName);

    boolean isEnabled();
}
