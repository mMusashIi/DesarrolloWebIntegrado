package com.backend.reporte_service.client;

import com.backend.reporte_service.model.dto.PagoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PagoClient {

    private final RestClient restClient;
    private final String pagoServiceUrl;

    public PagoClient(RestClient.Builder builder,
                      @Value("${pago.service.url:http://localhost:8085}") String pagoServiceUrl) {
        this.restClient = builder.build();
        this.pagoServiceUrl = pagoServiceUrl;
    }

    public List<PagoDTO> getPagosByReserva(Long idReserva, String jwtToken) {
        try {
            Map<?, ?> response = restClient.get()
                    .uri(pagoServiceUrl + "/api/pagos/reserva/{id}", idReserva)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .body(Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Object data = response.get("data");
                if (data instanceof List<?> list) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                    return mapper.convertValue(list,
                            mapper.getTypeFactory().constructCollectionType(List.class, PagoDTO.class));
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error al obtener pagos de pago-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
