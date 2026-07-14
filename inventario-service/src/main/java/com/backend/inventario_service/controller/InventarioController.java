package com.backend.inventario_service.controller;

import com.backend.inventario_service.model.dto.InventarioDTO;
import com.backend.inventario_service.model.dto.ResponseDTO;
import com.backend.inventario_service.model.mapper.InventarioMapper;
import com.backend.inventario_service.service.InventarioPaqueteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioPaqueteService inventarioPaqueteService;
    private final InventarioMapper inventarioMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getAllInventario() {
        log.info("Obteniendo todo el inventario");
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioPaqueteService.findAll());
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<InventarioDTO>> crearInventario(@RequestBody InventarioDTO request) {
        InventarioDTO creado = inventarioMapper.toDto(
                inventarioPaqueteService.save(inventarioMapper.toEntity(request)));
        return ResponseEntity.ok(ResponseDTO.success("Inventario creado exitosamente", creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<InventarioDTO>> actualizarInventario(
            @PathVariable Long id, @RequestBody InventarioDTO request) {
        InventarioDTO actualizado = inventarioMapper.toDto(
                inventarioPaqueteService.update(id, inventarioMapper.toEntity(request)));
        return ResponseEntity.ok(ResponseDTO.success("Inventario actualizado exitosamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> eliminarInventario(@PathVariable Long id) {
        inventarioPaqueteService.deleteById(id);
        return ResponseEntity.ok(ResponseDTO.success("Inventario eliminado exitosamente", null));
    }

    @GetMapping("/disponible")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getInventarioDisponible() {
        log.info("Obteniendo inventario disponible");
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioPaqueteService.findConCupoDisponible());
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    @GetMapping("/paquete/{idPaquete}")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getInventarioByPaquete(@PathVariable Long idPaquete) {
        log.info("Obteniendo inventario del paquete ID: {}", idPaquete);
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioPaqueteService.findByPaquete(idPaquete));
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    @GetMapping("/paquete/{idPaquete}/disponible")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getInventarioDisponibleByPaquete(@PathVariable Long idPaquete) {
        log.info("Obteniendo inventario disponible del paquete ID: {}", idPaquete);
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioPaqueteService.findInventarioDisponibleByPaquete(idPaquete));
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    @GetMapping("/proximas-salidas")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getProximasSalidas() {
        log.info("Obteniendo próximas salidas disponibles");
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioPaqueteService.findProximasSalidasDisponibles());
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    // --- Endpoints internos para reserva-service ---

    @GetMapping("/{id}/contexto-pago")
    public ResponseEntity<InventarioDTO> getContextoPago(@PathVariable Long id) {
        InventarioDTO inventario = inventarioPaqueteService.findById(id)
                .map(inventarioMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));
        return ResponseEntity.ok(inventario);
    }

    /**
     * Verifica si hay cupo disponible para el inventario indicado.
     * Llamado por reserva-service antes de confirmar una reserva.
     */
    @GetMapping("/{id}/verificar")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @PathVariable Long id, @RequestParam Integer cantidad) {
        log.info("Verificando disponibilidad para inventario ID: {}, cantidad: {}", id, cantidad);
        return ResponseEntity.ok(inventarioPaqueteService.verificarDisponibilidad(id, cantidad));
    }

    /**
     * Reduce el cupo disponible al confirmar una reserva.
     * Llamado por reserva-service.
     */
    @PutMapping("/{id}/reducir-cupo")
    public ResponseEntity<Void> reducirCupo(
            @PathVariable Long id, @RequestParam Integer cantidad) {
        log.info("Reduciendo cupo en {} para inventario ID: {}", cantidad, id);
        inventarioPaqueteService.reducirCupo(id, cantidad);
        return ResponseEntity.ok().build();
    }

    /**
     * Aumenta el cupo disponible al cancelar una reserva.
     * Llamado por reserva-service.
     */
    @PutMapping("/{id}/aumentar-cupo")
    public ResponseEntity<Void> aumentarCupo(
            @PathVariable Long id, @RequestParam Integer cantidad) {
        log.info("Aumentando cupo en {} para inventario ID: {}", cantidad, id);
        inventarioPaqueteService.aumentarCupo(id, cantidad);
        return ResponseEntity.ok().build();
    }
}
