package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.model.dto.PaqueteDTO;
import com.buganvilla.buganvillatours.model.dto.PaqueteDetailDTO;
import com.buganvilla.buganvillatours.model.dto.ResponseDTO;
import com.buganvilla.buganvillatours.model.entity.Paquete;
import com.buganvilla.buganvillatours.service.PaqueteService;
import com.buganvilla.buganvillatours.model.mapper.PaqueteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/paquetes")
@RequiredArgsConstructor
public class PaqueteController {

    private final PaqueteService paqueteService;
    private final PaqueteMapper paqueteMapper;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<PaqueteDTO>>> getAllPaquetes() {
        log.info("Obteniendo todos los paquetes");
        List<PaqueteDTO> paquetes = paqueteMapper.toDtoList(paqueteService.findAll());
        return ResponseEntity.ok(ResponseDTO.success(paquetes));
    }

    @GetMapping("/activos")
    public ResponseEntity<ResponseDTO<List<PaqueteDTO>>> getPaquetesActivos() {
        log.info("Obteniendo paquetes activos");
        List<PaqueteDTO> paquetes = paqueteMapper.toDtoList(paqueteService.findActivos());
        return ResponseEntity.ok(ResponseDTO.success(paquetes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<PaqueteDetailDTO>> getPaqueteById(@PathVariable Long id) {
        log.info("Obteniendo paquete con ID: {}", id);
        PaqueteDetailDTO paquete = paqueteService.findById(id)
                .map(paqueteMapper::toDetailDto)
                .orElse(null);
        return ResponseEntity.ok(ResponseDTO.success(paquete));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PaqueteDTO>> createPaquete(@RequestBody PaqueteDTO paqueteDTO) {
        log.info("Creando nuevo paquete: {}", paqueteDTO.getNombrePaquete());
        Paquete paquete = paqueteMapper.toEntity(paqueteDTO);
        Paquete paqueteGuardado = paqueteService.save(paquete);
        PaqueteDTO paqueteCreado = paqueteMapper.toDto(paqueteGuardado);
        return ResponseEntity.ok(ResponseDTO.success("Paquete creado exitosamente", paqueteCreado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PaqueteDTO>> updatePaquete(@PathVariable Long id, @RequestBody PaqueteDTO paqueteDTO) {
        log.info("Actualizando paquete con ID: {}", id);
        Paquete paquete = paqueteMapper.toEntity(paqueteDTO);
        Paquete paqueteActualizado = paqueteService.update(id, paquete);
        PaqueteDTO paqueteDTOActualizado = paqueteMapper.toDto(paqueteActualizado);
        return ResponseEntity.ok(ResponseDTO.success("Paquete actualizado exitosamente", paqueteDTOActualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> deletePaquete(@PathVariable Long id) {
        log.info("Eliminando paquete con ID: {}", id);
        paqueteService.deleteById(id);
        return ResponseEntity.ok(ResponseDTO.success("Paquete eliminado exitosamente", null));
    }
    // En PaqueteController - agregar estos endpoints
    @GetMapping("/public/activos")
    public ResponseEntity<ResponseDTO<List<PaqueteDTO>>> getPaquetesActivosPublicos() {
        log.info("Obteniendo paquetes activos para frontend");
        List<PaqueteDTO> paquetes = paqueteMapper.toDtoList(paqueteService.findActivos());
        return ResponseEntity.ok(ResponseDTO.success(paquetes));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ResponseDTO<PaqueteDetailDTO>> getPaquetePublico(@PathVariable Long id) {
        log.info("Obteniendo paquete público ID: {}", id);
        PaqueteDetailDTO paquete = paqueteService.findById(id)
                .map(paqueteMapper::toDetailDto)
                .orElse(null);
        return ResponseEntity.ok(ResponseDTO.success(paquete));
    }

    // Búsqueda con filtros para React
    @GetMapping("/public/buscar")
    public ResponseEntity<ResponseDTO<List<PaqueteDTO>>> buscarPaquetes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String ciudad) {

        log.info("Búsqueda de paquetes - nombre: {}, precio: {}-{}, ciudad: {}",
                nombre, precioMin, precioMax, ciudad);

        List<PaqueteDTO> paquetes = paqueteMapper.toDtoList(
                paqueteService.buscarConFiltros(nombre, precioMin, precioMax, "activo"));

        return ResponseEntity.ok(ResponseDTO.success(paquetes));
    }

    // Alias para compatibilidad con el frontend
    @GetMapping("/public/search")
    public ResponseEntity<ResponseDTO<List<PaqueteDTO>>> searchPaquetes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String estado) {
        return buscarPaquetes(nombre, precioMin, precioMax, null);
    }
}