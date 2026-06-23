package com.buganvilla.buganvillatours.service;

import com.buganvilla.buganvillatours.model.dto.api.ConsultaDniResponseDTO;
import com.buganvilla.buganvillatours.model.dto.api.ConsultaRucResponseDTO;
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
                .uri(baseUrl + "/dni?numero={numero}", numeroDni)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ConsultaDniResponseDTO.class);
    }

    public ConsultaRucResponseDTO consultarRuc(String numeroRuc) {
        return restClient.get()
                .uri(baseUrl + "/ruc?numero={numero}", numeroRuc)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(ConsultaRucResponseDTO.class);
    }
}
