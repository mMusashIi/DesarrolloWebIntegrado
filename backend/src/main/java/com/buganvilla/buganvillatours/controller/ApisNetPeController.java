package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.model.dto.api.ConsultaDniResponseDTO;
import com.buganvilla.buganvillatours.service.ApisNetPeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apis-net")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApisNetPeController {

    private final ApisNetPeService apisNetPeService;

    @GetMapping("/dni/{numero}")
    public ResponseEntity<?> getPersonaByDni(@PathVariable String numero) {
        try {
            ConsultaDniResponseDTO response = apisNetPeService.consultarDni(numero);
            if (response != null && response.getNombres() != null) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).body("Persona no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al consultar el DNI: " + e.getMessage());
        }
    }
}
