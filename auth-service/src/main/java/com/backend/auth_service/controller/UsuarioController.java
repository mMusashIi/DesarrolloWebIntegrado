package com.backend.auth_service.controller;

import com.backend.auth_service.model.dto.ResponseDTO;
import com.backend.auth_service.model.dto.UsuarioDTO;
import com.backend.auth_service.model.dto.UsuarioRequest;
import com.backend.auth_service.model.entity.Usuario;
import com.backend.auth_service.model.mapper.UsuarioMapper;
import com.backend.auth_service.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<List<UsuarioDTO>>> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios");
        List<UsuarioDTO> usuarios = usuarioMapper.toDtoList(usuarioService.findAll());
        return ResponseEntity.ok(ResponseDTO.success(usuarios));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> getUsuarioById(@PathVariable Long id) {
        log.info("Obteniendo usuario con ID: {}", id);
        UsuarioDTO usuario = usuarioService.findById(id)
                .map(usuarioMapper::toDto)
                .orElse(null);
        return ResponseEntity.ok(ResponseDTO.success(usuario));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> createUsuario(@RequestBody UsuarioRequest usuarioRequest) {
        log.info("Creando nuevo usuario: {}", usuarioRequest.getEmail());
        Usuario usuario = usuarioMapper.toEntity(usuarioRequest);
        Usuario usuarioGuardado = usuarioService.save(usuario);
        UsuarioDTO usuarioCreado = usuarioMapper.toDto(usuarioGuardado);
        return ResponseEntity.ok(ResponseDTO.success("Usuario creado exitosamente", usuarioCreado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> deleteUsuario(@PathVariable Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        usuarioService.deleteById(id);
        return ResponseEntity.ok(ResponseDTO.success("Usuario eliminado exitosamente", null));
    }
}
