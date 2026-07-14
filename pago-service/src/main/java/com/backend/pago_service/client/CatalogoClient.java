package com.backend.pago_service.client;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CatalogoClient {
    public record PaquetePago(Long idPaquete, String nombrePaquete, BigDecimal precioBase, String estado) {}
    private record ApiResponse<T>(boolean success, T data) {}

    private final RestClient client;
    private final String baseUrl;

    public CatalogoClient(RestClient.Builder builder,
            @Value("${catalogo.service.url:http://localhost:8082}") String baseUrl) {
        this.client = builder.build();
        this.baseUrl = baseUrl;
    }

    public PaquetePago obtenerParaPago(Long idPaquete) {
        ApiResponse<PaquetePago> response = client.get()
                .uri(baseUrl + "/api/paquetes/{id}", idPaquete)
                .retrieve().body(new ParameterizedTypeReference<ApiResponse<PaquetePago>>() {});
        if (response == null || response.data() == null) {
            throw new IllegalStateException("Paquete no encontrado");
        }
        return response.data();
    }
}
