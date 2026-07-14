package com.backend.reserva_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class InventarioClient {

    private final RestClient restClient;
    private final String inventarioServiceUrl;
    private final String internalToken;

    public InventarioClient(RestClient.Builder builder,
                            @Value("${inventario.service.url:http://localhost:8083}") String inventarioServiceUrl,
                            @Value("${app.internal-token:dev-internal-token-change-me}") String internalToken) {
        this.restClient = builder.build();
        this.inventarioServiceUrl = inventarioServiceUrl;
        this.internalToken = internalToken;
    }

    public boolean verificarDisponibilidad(Long idInventario, int cantidad) {
        try {
            Boolean result = restClient.get()
                    .uri(inventarioServiceUrl + "/api/inventario/{id}/verificar?cantidad={cantidad}",
                            idInventario, cantidad)
                    .header("X-Internal-Token", internalToken)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error al verificar disponibilidad en inventario-service: {}", e.getMessage());
            return false;
        }
    }

    public void reducirCupo(Long idInventario, int cantidad) {
        try {
            restClient.put()
                    .uri(inventarioServiceUrl + "/api/inventario/{id}/reducir-cupo?cantidad={cantidad}",
                            idInventario, cantidad)
                    .header("X-Internal-Token", internalToken)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error al reducir cupo en inventario-service: {}", e.getMessage());
            throw new RuntimeException("No se pudo actualizar el inventario: " + e.getMessage());
        }
    }

    public void aumentarCupo(Long idInventario, int cantidad) {
        try {
            restClient.put()
                    .uri(inventarioServiceUrl + "/api/inventario/{id}/aumentar-cupo?cantidad={cantidad}",
                            idInventario, cantidad)
                    .header("X-Internal-Token", internalToken)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Error al aumentar cupo en inventario-service: {}", e.getMessage());
        }
    }
}
