package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.model.dto.InventarioDTO;
import com.buganvilla.buganvillatours.model.dto.ResponseDTO;
import com.buganvilla.buganvillatours.service.InventarioPaqueteService;
import com.buganvilla.buganvillatours.model.mapper.InventarioMapper;
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

    private final InventarioPaqueteService inventarioService;
    private final InventarioMapper inventarioMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getAllInventario() {
        log.info("Obteniendo todo el inventario");
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioService.findAll());
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    @GetMapping("/disponible")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getInventarioDisponible() {
        log.info("Obteniendo inventario disponible");
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioService.findConCupoDisponible());
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    @GetMapping("/paquete/{idPaquete}")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getInventarioByPaquete(@PathVariable Long idPaquete) {
        log.info("Obteniendo inventario del paquete ID: {}", idPaquete);
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioService.findByPaquete(idPaquete));
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    @GetMapping("/paquete/{idPaquete}/disponible")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getInventarioDisponibleByPaquete(@PathVariable Long idPaquete) {
        log.info("Obteniendo inventario disponible del paquete ID: {}", idPaquete);
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioService.findInventarioDisponibleByPaquete(idPaquete));
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }

    @GetMapping("/proximas-salidas")
    public ResponseEntity<ResponseDTO<List<InventarioDTO>>> getProximasSalidas() {
        log.info("Obteniendo próximas salidas disponibles");
        List<InventarioDTO> inventario = inventarioMapper.toDtoList(inventarioService.findProximasSalidasDisponibles());
        return ResponseEntity.ok(ResponseDTO.success(inventario));
    }
}