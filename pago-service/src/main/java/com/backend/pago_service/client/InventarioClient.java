package com.backend.pago_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventarioClient {
    public record InventarioPago(Long idInventario, Long idPaquete) {}

    private final RestClient client;
    private final String baseUrl;
    private final String internalToken;

    public InventarioClient(RestClient.Builder builder,
            @Value("${inventario.service.url:http://localhost:8083}") String baseUrl,
            @Value("${app.internal-token:dev-internal-token-change-me}") String internalToken) {
        this.client = builder.build();
        this.baseUrl = baseUrl;
        this.internalToken = internalToken;
    }

    public InventarioPago obtenerParaPago(Long idInventario) {
        InventarioPago result = client.get()
                .uri(baseUrl + "/api/inventario/{id}/contexto-pago", idInventario)
                .header("X-Internal-Token", internalToken)
                .retrieve().body(InventarioPago.class);
        if (result == null) throw new IllegalStateException("Inventario no encontrado");
        return result;
    }
}
