package com.backend.pago_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
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

    public void notificarPago(String phone, String name, BigDecimal monto, String reservaId) {
        try {
            restClient.post()
                    .uri(notificacionServiceUrl + "/api/notificacion/pago")
                    .header("Content-Type", "application/json")
                    .header("X-Internal-Token", internalToken)
                    .body(Map.of("phone", phone, "name", name,
                            "amount", monto.toPlainString(), "reservaId", reservaId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de pago: {}", e.getMessage());
        }
    }
}
