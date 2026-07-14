package com.backend.reserva_service.controller;

import com.backend.reserva_service.model.dto.ReservaDTO;
import com.backend.reserva_service.model.dto.ReservaRequest;
import com.backend.reserva_service.model.dto.ResponseDTO;
import com.backend.reserva_service.model.entity.Reserva;
import com.backend.reserva_service.model.mapper.ReservaMapper;
import com.backend.reserva_service.service.ReservaService;
import com.backend.reserva_service.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaMapper reservaMapper;
    private final SecurityUtil securityUtil;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<ReservaDTO>>> getAllReservas() {
        List<ReservaDTO> reservas = reservaMapper.toDtoList(reservaService.findAll());
        return ResponseEntity.ok(ResponseDTO.success(reservas));
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<ResponseDTO<List<ReservaDTO>>> getMisReservas() {
        Long idUsuario = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        List<ReservaDTO> reservas = reservaMapper.toDtoList(reservaService.findByUsuario(idUsuario));
        return ResponseEntity.ok(ResponseDTO.success(reservas));
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDTO<List<ReservaDTO>>> getMyReservations() {
        return getMisReservas();
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #idUsuario.toString()")
    public ResponseEntity<ResponseDTO<List<ReservaDTO>>> getReservasByUsuario(@PathVariable Long idUsuario) {
        List<ReservaDTO> reservas = reservaMapper.toDtoList(reservaService.findByUsuario(idUsuario));
        return ResponseEntity.ok(ResponseDTO.success(reservas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<ReservaDTO>> getReservaById(@PathVariable Long id, Authentication authentication) {
        ReservaDTO reserva = reservaService.findById(id)
                .map(reservaMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        validarPropietarioOAdmin(reserva, authentication);
        return ResponseEntity.ok(ResponseDTO.success(reserva));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ReservaDTO>> createReserva(@Valid @RequestBody ReservaRequest reservaRequest,
                                                                  HttpServletRequest request) {
        Long idUsuario = securityUtil.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        String nombreComprador = obtenerNombreComprador(request, reservaRequest.getNombreComprador());
        Reserva reserva = Reserva.builder()
                .idUsuario(idUsuario)
                .nombreComprador(nombreComprador)
                .telefonoComprador(reservaRequest.getTelefonoComprador())
                .nombreCliente(reservaRequest.getNombreCliente())
                .nombresViajeros(reservaRequest.getNombresViajeros())
                .telefonosViajeros(reservaRequest.getTelefonosViajeros())
                .dniCliente(reservaRequest.getDniCliente())
                .emailCliente(reservaRequest.getEmailCliente())
                .telefonoCliente(reservaRequest.getTelefonoCliente())
                .whatsappOptIn(Boolean.TRUE.equals(reservaRequest.getWhatsappOptIn()))
                .idInventario(reservaRequest.getIdInventario())
                .nombrePaquete(reservaRequest.getNombrePaquete())
                .fechaViaje(reservaRequest.getFechaViaje())
                .cantidadPersonas(reservaRequest.getCantidadPersonas())
                .estado("pendiente")
                .build();

        Reserva guardada = reservaService.save(reserva);
        return ResponseEntity.ok(ResponseDTO.success("Reserva creada exitosamente", reservaMapper.toDto(guardada)));
    }

    private String obtenerNombreComprador(HttpServletRequest request, String fallback) {
        if (fallback != null && !fallback.isBlank()) return fallback.trim();
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return securityUtil.getFullNameFromToken(authorization.substring(7)).orElse(fallback);
        }
        return fallback;
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ResponseDTO<ReservaDTO>> cancelarReserva(@PathVariable Long id, Authentication authentication) {
        ReservaDTO actual = reservaService.findById(id).map(reservaMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        validarPropietarioOAdmin(actual, authentication);
        ReservaDTO reserva = reservaMapper.toDto(reservaService.cancelarReserva(id));
        return ResponseEntity.ok(ResponseDTO.success("Reserva cancelada exitosamente", reserva));
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<ResponseDTO<ReservaDTO>> confirmarReserva(@PathVariable Long id) {
        ReservaDTO reserva = reservaMapper.toDto(reservaService.confirmarReserva(id));
        return ResponseEntity.ok(ResponseDTO.success("Reserva confirmada exitosamente", reserva));
    }

    private void validarPropietarioOAdmin(ReservaDTO reserva, Authentication authentication) {
        // Las llamadas internas ya fueron autenticadas por X-Internal-Token en SecurityConfig.
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) return;
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!admin && !String.valueOf(reserva.getIdUsuario()).equals(authentication.getName())) {
            throw new AccessDeniedException("La reserva no pertenece al usuario autenticado");
        }
    }
}
