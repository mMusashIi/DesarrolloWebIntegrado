package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.model.dto.LugarDTO;
import com.buganvilla.buganvillatours.model.dto.ResponseDTO;
import com.buganvilla.buganvillatours.model.entity.Lugar;
import com.buganvilla.buganvillatours.service.LugarService;
import com.buganvilla.buganvillatours.model.mapper.LugarMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lugares")
@RequiredArgsConstructor
public class LugarController {

    private final LugarService lugarService;
    private final LugarMapper lugarMapper;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<LugarDTO>>> getAllLugares() {
        log.info("Obteniendo todos los lugares");
        List<LugarDTO> lugares = lugarMapper.toDtoList(lugarService.findAll());
        return ResponseEntity.ok(ResponseDTO.success(lugares));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<LugarDTO>> getLugarById(@PathVariable Long id) {
        log.info("Obteniendo lugar con ID: {}", id);
        LugarDTO lugar = lugarService.findById(id)
                .map(lugarMapper::toDto)
                .orElse(null);
        return ResponseEntity.ok(ResponseDTO.success(lugar));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<LugarDTO>> createLugar(@RequestBody LugarDTO lugarDTO) {
        log.info("Creando nuevo lugar: {}", lugarDTO.getNombreLugar());
        Lugar lugar = lugarMapper.toEntity(lugarDTO);
        Lugar lugarGuardado = lugarService.save(lugar);
        LugarDTO lugarCreado = lugarMapper.toDto(lugarGuardado);
        return ResponseEntity.ok(ResponseDTO.success("Lugar creado exitosamente", lugarCreado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<LugarDTO>> updateLugar(@PathVariable Long id, @RequestBody LugarDTO lugarDTO) {
        log.info("Actualizando lugar con ID: {}", id);
        // Convertir DTO a Entity para el update
        Lugar lugar = lugarMapper.toEntity(lugarDTO);
        Lugar lugarActualizado = lugarService.update(id, lugar);
        LugarDTO lugarDTOActualizado = lugarMapper.toDto(lugarActualizado);
        return ResponseEntity.ok(ResponseDTO.success("Lugar actualizado exitosamente", lugarDTOActualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> deleteLugar(@PathVariable Long id) {
        log.info("Eliminando lugar con ID: {}", id);
        lugarService.deleteById(id);
        return ResponseEntity.ok(ResponseDTO.success("Lugar eliminado exitosamente", null));
    }
}