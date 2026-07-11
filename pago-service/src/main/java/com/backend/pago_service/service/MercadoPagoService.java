package com.backend.pago_service.service;

import com.backend.pago_service.client.NotificacionClient;
import com.backend.pago_service.client.ReservaClient;
import com.backend.pago_service.client.InventarioClient;
import com.backend.pago_service.client.CatalogoClient;
import com.backend.pago_service.model.dto.payment.PreferenceResponseDTO;
import com.backend.pago_service.model.entity.Pago;
import com.backend.pago_service.repository.PagoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    private final PagoRepository pagoRepository;
    private final ReservaClient reservaClient;
    private final NotificacionClient notificacionClient;
    private final InventarioClient inventarioClient;
    private final CatalogoClient catalogoClient;

    @Value("${mercadopago.back-url.success}")
    private String backUrlSuccess;

    @Value("${mercadopago.back-url.failure}")
    private String backUrlFailure;

    @Value("${mercadopago.back-url.pending}")
    private String backUrlPending;

    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;

    @Transactional
    public PreferenceResponseDTO createPreference(Long reservaId, Long solicitanteId, boolean esAdmin)
            throws MPException, MPApiException {
        ReservaClient.ReservaPago reserva = reservaClient.obtenerParaPago(reservaId);
        if (!esAdmin && !reserva.idUsuario().equals(solicitanteId)) {
            throw new org.springframework.security.access.AccessDeniedException("La reserva no pertenece al usuario autenticado");
        }
        if (!"pendiente".equalsIgnoreCase(reserva.estado())) {
            throw new IllegalStateException("Solo se pueden pagar reservas pendientes");
        }
        InventarioClient.InventarioPago inventario = inventarioClient.obtenerParaPago(reserva.idInventario());
        CatalogoClient.PaquetePago paquete = catalogoClient.obtenerParaPago(inventario.idPaquete());
        if (paquete.precioBase() == null || paquete.precioBase().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("El paquete no tiene un precio válido");
        }
        int cantidad = Math.max(reserva.cantidadPersonas(), 1);
        BigDecimal precioUnitario = paquete.precioBase();
        BigDecimal monto = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(paquete.nombrePaquete())
                .description("Reserva #" + reservaId)
                .quantity(Math.max(cantidad, 1))
                .unitPrice(precioUnitario)
                .currencyId("PEN")
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(backUrlSuccess)
                .failure(backUrlFailure)
                .pending(backUrlPending)
                .build();

        PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                .items(List.of(item))
                .backUrls(backUrls)
                .externalReference(String.valueOf(reservaId));

        if (notificationUrl != null && !notificationUrl.isBlank()) {
            builder.notificationUrl(notificationUrl);
        }

        PreferenceClient client = new PreferenceClient();
        Preference preference;
        try {
            preference = client.create(builder.build());
        } catch (MPApiException e) {
            log.error("Error MP API: codigo={}", e.getStatusCode());
            throw e;
        }
        log.info("Preferencia MP creada: {} para reserva {}", preference.getId(), reservaId);

        Pago pago = Pago.builder()
                .idReserva(reservaId)
                .monto(monto)
                .metodo("mercadopago")
                .estado("pendiente")
                .mercadoPagoPreferenceId(preference.getId())
                .build();
        pago = pagoRepository.save(pago);

        String initPoint = preference.getSandboxInitPoint() != null
                ? preference.getSandboxInitPoint()
                : preference.getInitPoint();

        return PreferenceResponseDTO.builder()
                .initPoint(initPoint)
                .preferenceId(preference.getId())
                .pagoId(pago.getIdPago())
                .reservaId(reservaId)
                .build();
    }

    @Transactional
    public void procesarWebhook(String paymentId) throws MPException, MPApiException {
        log.info("Procesando webhook MP para paymentId: {}", paymentId);

        PaymentClient paymentClient = new PaymentClient();
        Payment payment = paymentClient.get(Long.parseLong(paymentId));

        String mpStatus = payment.getStatus();
        String externalRef = payment.getExternalReference();

        log.info("Pago MP {}: status={}, externalRef={}", paymentId, mpStatus, externalRef);

        if (externalRef == null || externalRef.isBlank()) {
            log.warn("Webhook sin external_reference, ignorando paymentId={}", paymentId);
            return;
        }

        Long reservaId = Long.parseLong(externalRef);

        Pago pago = pagoRepository.findByIdReserva(reservaId)
                .stream()
                .filter(p -> "pendiente".equals(p.getEstado()) && "mercadopago".equals(p.getMetodo()))
                .findFirst()
                .orElse(null);

        if (pago == null) {
            log.warn("No se encontró pago pendiente de mercadopago para reservaId={}", reservaId);
            return;
        }

        if (paymentId.equals(pago.getMercadoPagoPaymentId())) {
            log.info("PaymentId {} ya fue procesado, ignorando.", paymentId);
            return;
        }

        pago.setMercadoPagoPaymentId(paymentId);
        pago.setMercadoPagoStatus(mpStatus);

        switch (mpStatus) {
            case "approved" -> {
                log.info("Pago {} aprobado → confirmando reserva {}", paymentId, reservaId);
                pago.procesarPago();
                reservaClient.confirmarReserva(reservaId);
                ReservaClient.ReservaPago reserva = reservaClient.obtenerParaPago(reservaId);
                if (Boolean.TRUE.equals(reserva.whatsappOptIn())) {
                    notificacionClient.notificarPago(reserva.telefonoComprador(), reserva.nombreComprador(),
                            pago.getMonto(), "RES-" + reservaId);
                }
            }
            case "rejected", "cancelled" -> {
                log.info("Pago {} rechazado/cancelado", paymentId);
                pago.rechazarPago();
                reservaClient.cancelarReserva(reservaId);
            }
            case "pending", "in_process" -> {
                log.info("Pago {} en proceso.", paymentId);
                pago.setEstado("en_proceso");
            }
            default -> log.warn("Estado MP desconocido '{}' para paymentId={}", mpStatus, paymentId);
        }

        pagoRepository.save(pago);
    }
}
