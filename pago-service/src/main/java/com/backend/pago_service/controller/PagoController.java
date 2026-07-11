package com.backend.pago_service.controller;

import com.backend.pago_service.model.dto.PagoDTO;
import com.backend.pago_service.model.dto.PagoRequest;
import com.backend.pago_service.model.dto.ResponseDTO;
import com.backend.pago_service.model.entity.Pago;
import com.backend.pago_service.model.mapper.PagoMapper;
import com.backend.pago_service.service.PagoService;
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
        return ResponseEntity.ok(ResponseDTO.success(pagoMapper.toDtoList(pagoService.findAll())));
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<ResponseDTO<List<PagoDTO>>> getPagosByReserva(@PathVariable Long reservaId) {
        return ResponseEntity.ok(ResponseDTO.success(pagoMapper.toDtoList(pagoService.findByReserva(reservaId))));
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<PagoDTO>> crearPago(@RequestBody PagoRequest pagoRequest) {
        Pago pago = pagoMapper.toEntity(pagoRequest);
        PagoDTO created = pagoMapper.toDto(pagoService.save(pago));
        return ResponseEntity.ok(ResponseDTO.success("Pago creado exitosamente", created));
    }

    @PutMapping("/{id}/procesar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PagoDTO>> procesarPago(@PathVariable Long id) {
        PagoDTO pago = pagoMapper.toDto(pagoService.procesarPago(id));
        return ResponseEntity.ok(ResponseDTO.success("Pago procesado exitosamente", pago));
    }
}
