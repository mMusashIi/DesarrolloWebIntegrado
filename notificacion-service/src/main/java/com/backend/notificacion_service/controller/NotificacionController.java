package com.backend.notificacion_service.controller;

import com.backend.notificacion_service.model.dto.NotificacionCancelacionRequest;
import com.backend.notificacion_service.model.dto.NotificacionPagoRequest;
import com.backend.notificacion_service.model.dto.NotificacionReservaRequest;
import com.backend.notificacion_service.whatsapp.WhatsAppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificacion")
@RequiredArgsConstructor
public class NotificacionController {

    private final WhatsAppNotificationService whatsAppNotificationService;

    @PostMapping("/reserva")
    public ResponseEntity<Void> notificarReserva(@RequestBody NotificacionReservaRequest request) {
        whatsAppNotificationService.notifyReservationConfirmation(
                request.getPhone(), request.getName(), request.getPackageName(),
                request.getDate(), request.getPersons(), request.getReservaId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pago")
    public ResponseEntity<Void> notificarPago(@RequestBody NotificacionPagoRequest request) {
        whatsAppNotificationService.notifyPaymentConfirmation(
                request.getPhone(), request.getName(), request.getAmount(), request.getReservaId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancelacion")
    public ResponseEntity<Void> notificarCancelacion(@RequestBody NotificacionCancelacionRequest request) {
        whatsAppNotificationService.notifyReservationCancellation(
                request.getPhone(), request.getName(), request.getPackageName());
        return ResponseEntity.ok().build();
    }
}
