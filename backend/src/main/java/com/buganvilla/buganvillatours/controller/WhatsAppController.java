package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.service.OpenWAService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    private final OpenWAService openWAService;

    public WhatsAppController(OpenWAService openWAService) {
        this.openWAService = openWAService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, String>> enviarMensaje(
            @RequestParam String telefono,
            @RequestBody Map<String, String> body) {
        
        try {
            String mensaje = body.getOrDefault("mensaje", "");
            String response = openWAService.sendTextMessage(telefono, mensaje);
            
            Map<String, String> result = new HashMap<>();
            result.put("status", "success");
            result.put("response", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
