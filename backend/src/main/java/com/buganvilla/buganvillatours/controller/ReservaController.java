package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.model.dto.ReservaDTO;
import com.buganvilla.buganvillatours.model.dto.ReservaRequest;
import com.buganvilla.buganvillatours.model.dto.ResponseDTO;
import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.model.entity.Usuario;
import com.buganvilla.buganvillatours.service.InventarioPaqueteService;
import com.buganvilla.buganvillatours.service.ReservaService;
import com.buganvilla.buganvillatours.model.mapper.ReservaMapper;
import com.buganvilla.buganvillatours.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaMapper reservaMapper;
    private final SecurityUtil securityUtil;
    private final InventarioPaqueteService inventarioPaqueteService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<ReservaDTO>>> getAllReservas() {
        log.info("Obteniendo todas las reservas");
        List<ReservaDTO> reservas = reservaMapper.toDtoList(reservaService.findAll());
        return ResponseEntity.ok(ResponseDTO.success(reservas));
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<ResponseDTO<List<ReservaDTO>>> getMisReservas() {
        log.info("Obteniendo reservas del usuario actual");
        Usuario usuario = securityUtil.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        List<ReservaDTO> reservas = reservaMapper.toDtoList(reservaService.findByUsuario(usuario.getIdUsuario()));
        return ResponseEntity.ok(ResponseDTO.success(reservas));
    }

    // Alias para compatibilidad con el frontend
    @GetMapping("/my")
    public ResponseEntity<ResponseDTO<List<ReservaDTO>>> getMyReservations() {
        return getMisReservas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReservaDTO>> getReservaById(@PathVariable Long id) {
        log.info("Obteniendo reserva con ID: {}", id);
        ReservaDTO reserva = reservaService.findById(id)
                .map(reservaMapper::toDto)
                .orElse(null);
        return ResponseEntity.ok(ResponseDTO.success(reserva));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ReservaDTO>> createReserva(@RequestBody ReservaRequest reservaRequest) {
        log.info("Creando nueva reserva");
        
        // Obtener usuario actual
        Usuario usuario = securityUtil.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        
        // Obtener inventario
        InventarioPaquete inventario = inventarioPaqueteService.findById(reservaRequest.getIdInventario())
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        
        // Crear entidad Reserva
        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .inventario(inventario)
                .cantidadPersonas(reservaRequest.getCantidadPersonas())
                .estado("pendiente")
                .build();
        
        // Guardar reserva (el servicio maneja la verificación de disponibilidad y reducción de cupo)
        Reserva reservaGuardada = reservaService.save(reserva);
        
        ReservaDTO reservaCreada = reservaMapper.toDto(reservaGuardada);
        return ResponseEntity.ok(ResponseDTO.success("Reserva creada exitosamente", reservaCreada));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ResponseDTO<ReservaDTO>> cancelarReserva(@PathVariable Long id) {
        log.info("Cancelando reserva con ID: {}", id);
        ReservaDTO reserva = reservaMapper.toDto(reservaService.cancelarReserva(id));
        return ResponseEntity.ok(ResponseDTO.success("Reserva cancelada exitosamente", reserva));
    }
}