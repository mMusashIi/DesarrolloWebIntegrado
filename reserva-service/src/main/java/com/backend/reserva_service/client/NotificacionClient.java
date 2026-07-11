package com.backend.reserva_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class NotificacionClient {

    private final RestClient restClient;
    private final String notificacionServiceUrl;
    private final String internalToken;

    public NotificacionClient(RestClient.Builder builder,
                              @Value("${notificacion.service.url:http://localhost:8086}") String notificacionServiceUrl,
                              @Value("${app.internal-token:dev-internal-token-change-me}") String internalToken) {
        this.restClient = builder.build();
        this.notificacionServiceUrl = notificacionServiceUrl;
        this.internalToken = internalToken;
    }

    public void notificarReserva(String phone, String name, String packageName, String date, int persons,
                                 Long idReserva) {
        try {
            restClient.post()
                    .uri(notificacionServiceUrl + "/api/notificacion/reserva")
                    .header("Content-Type", "application/json")
                    .header("X-Internal-Token", internalToken)
                    .body(Map.of("phone", phone, "name", name, "packageName", packageName,
                            "date", date, "persons", persons, "reservaId", String.valueOf(idReserva)))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("No se pudo enviar notificacion de reserva: {}", e.getMessage());
        }
    }

    public void notificarCancelacion(String phone, String name, String packageName) {
        try {
            restClient.post()
                    .uri(notificacionServiceUrl + "/api/notificacion/cancelacion")
                    .header("Content-Type", "application/json")
                    .header("X-Internal-Token", internalToken)
                    .body(Map.of("phone", phone, "name", name, "packageName", packageName))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("No se pudo enviar notificacion de cancelacion: {}", e.getMessage());
        }
    }
}
