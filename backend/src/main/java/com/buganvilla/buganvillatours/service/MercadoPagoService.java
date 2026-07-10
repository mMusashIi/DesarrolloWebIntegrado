package com.buganvilla.buganvillatours.service;

import com.buganvilla.buganvillatours.model.dto.payment.PreferenceResponseDTO;
import com.buganvilla.buganvillatours.model.entity.Pago;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.model.entity.Usuario;
import com.buganvilla.buganvillatours.repository.PagoRepository;
import com.buganvilla.buganvillatours.repository.ReservaRepository;
import com.buganvilla.buganvillatours.whatsapp.WhatsAppNotificationService;
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

    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final WhatsAppNotificationService whatsAppNotificationService;

    @Value("${mercadopago.back-url.success}")
    private String backUrlSuccess;

    @Value("${mercadopago.back-url.failure}")
    private String backUrlFailure;

    @Value("${mercadopago.back-url.pending}")
    private String backUrlPending;

    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;

    // ────────────────────────────────────────────────────────────────────────
    // Crear preferencia de pago a partir de una Reserva existente
    // ────────────────────────────────────────────────────────────────────────
    @Transactional
    public PreferenceResponseDTO createPreference(Long reservaId) throws MPException, MPApiException {

        // 1. Obtener la reserva con todos sus datos
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + reservaId));

        if (!"pendiente".equals(reserva.getEstado())) {
            throw new IllegalStateException(
                    "Solo se pueden pagar reservas en estado 'pendiente'. Estado actual: " + reserva.getEstado());
        }

        // 2. Calcular monto total (precio_base × cantidad_personas)
        BigDecimal precioUnitario = reserva.getInventario().getPaquete().getPrecioBase();
        int cantidad = reserva.getCantidadPersonas();
        BigDecimal montoTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        String nombrePaquete = reserva.getInventario().getPaquete().getNombrePaquete();

        // 3. Construir el item de la preferencia
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(nombrePaquete)
                .description("Reserva #" + reservaId + " — " + cantidad + " persona(s)")
                .quantity(cantidad)
                .unitPrice(precioUnitario)
                .currencyId("PEN")
                .build();

        // 4. Configurar URLs de retorno
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(backUrlSuccess)
                .failure(backUrlFailure)
                .pending(backUrlPending)
                .build();

        // 5. Construir la preferencia
        PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
                .items(List.of(item))
                .backUrls(backUrls)
                // autoReturn eliminado: requiere back_url.success con URL pública (no funciona con localhost)
                // En producción, descomentar: .autoReturn("approved")
                .externalReference(String.valueOf(reservaId)); // para identificar la reserva en el webhook

        // Solo agrega notification_url si está configurada (requiere URL pública)
        if (notificationUrl != null && !notificationUrl.isBlank()) {
            builder.notificationUrl(notificationUrl);
        }

        PreferenceRequest preferenceRequest = builder.build();

        // 6. Crear la preferencia en MercadoPago
        PreferenceClient client = new PreferenceClient();
        Preference preference;
        try {
            preference = client.create(preferenceRequest);
        } catch (MPApiException e) {
            log.error("Error de la API de MercadoPago al crear preferencia: Codigo={}, Contenido={}", 
                    e.getStatusCode(), e.getApiResponse() != null ? e.getApiResponse().getContent() : "Sin contenido");
            throw e;
        }
        log.info("Preferencia MP creada: {} para reserva {}", preference.getId(), reservaId);

        // 7. Registrar el Pago en la BD local con estado "pendiente"
        Pago pago = Pago.builder()
                .reserva(reserva)
                .monto(montoTotal)
                .metodo("mercadopago")
                .estado("pendiente")
                .mercadoPagoPreferenceId(preference.getId())
                .build();
        pago = pagoRepository.save(pago);
        log.info("Pago local creado con ID: {}", pago.getIdPago());

        // 8. Devolver respuesta con la URL de pago
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

    // ────────────────────────────────────────────────────────────────────────
    // Procesar notificación webhook IPN de MercadoPago
    // ────────────────────────────────────────────────────────────────────────
    @Transactional
    public void procesarWebhook(String paymentId) throws MPException, MPApiException {

        log.info("Procesando webhook MP para paymentId: {}", paymentId);

        // 1. Consultar el pago directamente a la API de MP
        PaymentClient paymentClient = new PaymentClient();
        Payment payment = paymentClient.get(Long.parseLong(paymentId));

        String mpStatus = payment.getStatus();          // approved | pending | rejected | cancelled
        String externalRef = payment.getExternalReference(); // es el reservaId que pusimos

        log.info("Pago MP {}: status={}, externalRef={}", paymentId, mpStatus, externalRef);

        if (externalRef == null || externalRef.isBlank()) {
            log.warn("Webhook recibido sin external_reference, ignorando paymentId={}", paymentId);
            return;
        }

        Long reservaId = Long.parseLong(externalRef);

        // 2. Buscar el pago local asociado a la preferencia de esta reserva
        Pago pago = pagoRepository.findByReservaIdReserva(reservaId)
                .stream()
                .filter(p -> "pendiente".equals(p.getEstado()) && "mercadopago".equals(p.getMetodo()))
                .findFirst()
                .orElse(null);

        if (pago == null) {
            log.warn("No se encontró pago pendiente de mercadopago para reservaId={}", reservaId);
            return;
        }

        // Evitar procesar el mismo pago dos veces
        if (paymentId.equals(pago.getMercadoPagoPaymentId())) {
            log.info("PaymentId {} ya fue procesado, ignorando.", paymentId);
            return;
        }

        // 3. Actualizar campos MP en el registro local
        pago.setMercadoPagoPaymentId(paymentId);
        pago.setMercadoPagoStatus(mpStatus);

        // 4. Actualizar estado según la respuesta de MP
        switch (mpStatus) {
            case "approved" -> {
                log.info("Pago {} aprobado → confirmando reserva {}", paymentId, reservaId);
                pago.procesarPago();
                
                // Enviar confirmación por WhatsApp (async, no bloquea el webhook)
                Usuario usuario = pago.getReserva().getUsuario();
                if (usuario != null && usuario.getTelefono() != null) {
                    whatsAppNotificationService.notifyPaymentConfirmation(
                            usuario.getTelefono(),
                            usuario.getNombre(),
                            pago.getMonto(),
                            String.valueOf(reservaId));
                }
            }
            case "rejected", "cancelled" -> {
                log.info("Pago {} rechazado/cancelado → rechazando pago {}", paymentId, pago.getIdPago());
                pago.rechazarPago();
            }
            case "pending", "in_process" -> {
                log.info("Pago {} pendiente de acreditación.", paymentId);
                pago.setEstado("en_proceso");
            }
            default -> log.warn("Estado MP desconocido '{}' para paymentId={}", mpStatus, paymentId);
        }

        pagoRepository.save(pago);
        log.info("Pago {} actualizado correctamente.", pago.getIdPago());
    }
}
