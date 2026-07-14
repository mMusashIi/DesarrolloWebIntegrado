package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.model.dto.PagoDTO;
import com.buganvilla.buganvillatours.model.dto.PagoRequest;
import com.buganvilla.buganvillatours.model.dto.ResponseDTO;
import com.buganvilla.buganvillatours.service.PagoService;
import com.buganvilla.buganvillatours.model.mapper.PagoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final PagoMapper pagoMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<PagoDTO>>> getAllPagos() {
        log.info("Obteniendo todos los pagos");
        List<PagoDTO> pagos = pagoMapper.toDtoList(pagoService.findAll());
        return ResponseEntity.ok(ResponseDTO.success(pagos));
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<ResponseDTO<List<PagoDTO>>> getPagosByReserva(@PathVariable Long reservaId) {
        log.info("Obteniendo pagos de la reserva ID: {}", reservaId);
        List<PagoDTO> pagos = pagoMapper.toDtoList(pagoService.findByReserva(reservaId));
        return ResponseEntity.ok(ResponseDTO.success(pagos));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<PagoDTO>> crearPago(@RequestBody PagoRequest pagoRequest) {
        log.info("Creando nuevo pago");
        var pago = pagoMapper.toEntity(pagoRequest);
        PagoDTO pagoCreado = pagoMapper.toDto(pagoService.save(pago));
        return ResponseEntity.ok(ResponseDTO.success("Pago creado exitosamente", pagoCreado));
    }

    @PutMapping("/{id}/procesar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PagoDTO>> procesarPago(@PathVariable Long id) {
        log.info("Procesando pago con ID: {}", id);
        PagoDTO pago = pagoMapper.toDto(pagoService.procesarPago(id));
        return ResponseEntity.ok(ResponseDTO.success("Pago procesado exitosamente", pago));
    }
}