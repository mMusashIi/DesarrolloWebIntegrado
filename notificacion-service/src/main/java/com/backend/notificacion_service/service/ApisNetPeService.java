package com.backend.notificacion_service.service;

import com.backend.notificacion_service.model.dto.api.ConsultaDniResponseDTO;
import com.backend.notificacion_service.model.dto.api.ConsultaRucResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ApisNetPeService {

    private final RestClient restClient;

    @Value("${apis.net.pe.base-url}")
    private String baseUrl;

    @Value("${apis.net.pe.token}")
    private String token;

    public ApisNetPeService(RestClient restClient) {
        this.restClient = restClient;
    }

    public ConsultaDniResponseDTO consultarDni(String numeroDni) {
        return restClient.get()
                .uri(baseUrl + "/reniec/dni?numero={numero}", numeroDni)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ConsultaDniResponseDTO.class);
    }

    public ConsultaRucResponseDTO consultarRuc(String numeroRuc) {
        return restClient.get()
                .uri(baseUrl + "/sunat/ruc?numero={numero}", numeroRuc)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ConsultaRucResponseDTO.class);
    }
}
