package com.buganvilla.buganvillatours.service;

import com.buganvilla.buganvillatours.model.dto.whatsapp.WhatsAppMessageRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenWAService {

    private final RestClient restClient;

    @Value("${openwa.base-url}")
    private String baseUrl;

    @Value("${openwa.api-key}")
    private String apiKey;

    public OpenWAService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String sendTextMessage(String phone, String message) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Número inválido";
        }

        // Limpiar el número (quitar espacios, guiones, o signos +)
        String cleanPhone = phone.replaceAll("[^0-9]", "");

        // Si es un número peruano de 9 dígitos (ej. 987654321), le agregamos el código de país (51)
        if (cleanPhone.length() == 9) {
            cleanPhone = "51" + cleanPhone;
        }

        // OpenWA requiere el sufijo @c.us para chats individuales
        String chatId = cleanPhone.contains("@c.us") ? cleanPhone : cleanPhone + "@c.us";

        WhatsAppMessageRequestDTO request = WhatsAppMessageRequestDTO.builder()
                .chatId(chatId)
                .text(message)
                .session("default")
                .build();

        try {
            return restClient.post()
                    .uri(baseUrl + "/sendText")
                    .header("api_key", apiKey)
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            // Manejamos la excepción para que si WhatsApp falla, no bloquee la creación de la reserva o pago
            System.err.println("Error al enviar mensaje de WhatsApp: " + e.getMessage());
            return "Error al enviar mensaje";
        }
    }
}
