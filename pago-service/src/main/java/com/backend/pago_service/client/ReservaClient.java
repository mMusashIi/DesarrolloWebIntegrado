package com.backend.pago_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class ReservaClient {

    public record ReservaPago(Long idReserva, Long idUsuario, Long idInventario,
                              Integer cantidadPersonas, String estado, String nombreCliente,
                              String telefonoCliente, String nombreComprador, String telefonoComprador,
                              Boolean whatsappOptIn) {}

    private record ApiResponse<T>(boolean success, T data) {}

    private final RestClient restClient;
    private final String reservaServiceUrl;
    private final String internalToken;

    public ReservaClient(RestClient.Builder builder,
                         @Value("${reserva.service.url:http://localhost:8084}") String reservaServiceUrl,
                         @Value("${app.internal-token:dev-internal-token-change-me}") String internalToken) {
        this.restClient = builder.build();
        this.reservaServiceUrl = reservaServiceUrl;
        this.internalToken = internalToken;
    }

    public void confirmarReserva(Long idReserva) {
        try {
            restClient.put()
                    .uri(reservaServiceUrl + "/api/reservas/{id}/confirmar", idReserva)
                    .header("X-Internal-Token", internalToken)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Reserva {} confirmada via reserva-service", idReserva);
        } catch (Exception e) {
            log.error("Error al confirmar reserva {} en reserva-service: {}", idReserva, e.getMessage());
            throw new RuntimeException("No se pudo confirmar la reserva: " + e.getMessage());
        }
    }

    public ReservaPago obtenerParaPago(Long idReserva) {
        ApiResponse<ReservaPago> response = restClient.get()
                .uri(reservaServiceUrl + "/api/reservas/{id}", idReserva)
                .header("X-Internal-Token", internalToken)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<ApiResponse<ReservaPago>>() {});
        if (response == null || response.data() == null) {
            throw new IllegalStateException("Reserva no encontrada");
        }
        return response.data();
    }

    public void cancelarReserva(Long idReserva) {
        try {
            restClient.put()
                    .uri(reservaServiceUrl + "/api/reservas/{id}/cancelar", idReserva)
                    .header("X-Internal-Token", internalToken)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Reserva {} cancelada via reserva-service", idReserva);
        } catch (Exception e) {
            log.warn("No se pudo cancelar reserva {} en reserva-service: {}", idReserva, e.getMessage());
        }
    }
}
