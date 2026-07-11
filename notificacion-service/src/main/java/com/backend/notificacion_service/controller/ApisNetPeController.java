package com.backend.notificacion_service.controller;

import com.backend.notificacion_service.model.dto.api.ConsultaDniResponseDTO;
import com.backend.notificacion_service.service.ApisNetPeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apis-net")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApisNetPeController {

    private final ApisNetPeService apisNetPeService;

    @GetMapping("/dni/{numero}")
    public ResponseEntity<?> getPersonaByDni(@PathVariable String numero) {
        if (!numero.matches("\\d{8}")) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "El DNI debe contener exactamente 8 dígitos"));
        }
        try {
            ConsultaDniResponseDTO response = apisNetPeService.consultarDni(numero);
            if (response != null && response.getNombres() != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body(java.util.Map.of("message", "Persona no encontrada"));
            }
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(java.util.Map.of("message", "APIS.NET rechazó las credenciales configuradas"));
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(404).body(java.util.Map.of("message", "Persona no encontrada"));
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(java.util.Map.of("message", "No se pudo consultar APIS.NET"));
        }
    }

    @GetMapping("/ruc/{numero}")
    public ResponseEntity<?> getEmpresaByRuc(@PathVariable String numero) {
        try {
            var response = apisNetPeService.consultarRuc(numero);
            if (response != null && response.getNombre() != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body("Empresa no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al consultar el RUC: " + e.getMessage());
        }
    }
}
