package com.backend.reporte_service.client;

import com.backend.reporte_service.model.dto.ReservaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ReservaClient {

    private final RestClient restClient;
    private final String reservaServiceUrl;

    public ReservaClient(RestClient.Builder builder,
                         @Value("${reserva.service.url:http://localhost:8084}") String reservaServiceUrl) {
        this.restClient = builder.build();
        this.reservaServiceUrl = reservaServiceUrl;
    }

    public List<ReservaDTO> getAllReservas(String jwtToken) {
        try {
            // ResponseDTO<List<ReservaDTO>> wrapping
            Map<?, ?> response = restClient.get()
                    .uri(reservaServiceUrl + "/api/reservas")
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .body(Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Object data = response.get("data");
                if (data instanceof List<?> list) {
                    // Convert to List<ReservaDTO> via Jackson
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                    mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    return mapper.convertValue(list,
                            mapper.getTypeFactory().constructCollectionType(List.class, ReservaDTO.class));
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error al obtener reservas de reserva-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<ReservaDTO> getReservasByUsuario(Long idUsuario, String jwtToken) {
        try {
            Map<?, ?> response = restClient.get()
                    .uri(reservaServiceUrl + "/api/reservas/usuario/{id}", idUsuario)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .body(Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Object data = response.get("data");
                if (data instanceof List<?> list) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                    mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    return mapper.convertValue(list,
                            mapper.getTypeFactory().constructCollectionType(List.class, ReservaDTO.class));
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error al obtener reservas por usuario: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
