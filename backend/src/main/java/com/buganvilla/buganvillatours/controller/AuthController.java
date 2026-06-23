package com.buganvilla.buganvillatours.controller;

import com.buganvilla.buganvillatours.model.dto.AuthDTO;
import com.buganvilla.buganvillatours.model.dto.ResponseDTO;
import com.buganvilla.buganvillatours.model.dto.UsuarioDTO;
import com.buganvilla.buganvillatours.model.entity.Usuario;
import com.buganvilla.buganvillatours.security.JwtTokenUtil;
import com.buganvilla.buganvillatours.service.UsuarioService;
import com.buganvilla.buganvillatours.model.mapper.UsuarioMapper;
import com.buganvilla.buganvillatours.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<AuthDTO.LoginResponse>> login(@Valid @RequestBody AuthDTO.LoginRequest loginRequest) {
        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Obtener UserDetails del authentication
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generar token CORREGIDO
            String token = jwtTokenUtil.generateToken(userDetails);

            // Obtener usuario desde la base de datos
            Usuario usuario = usuarioService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UsuarioDTO usuarioDTO = usuarioMapper.toDto(usuario);
            AuthDTO.LoginResponse loginResponse = new AuthDTO.LoginResponse(token, usuarioDTO);

            return ResponseEntity.ok(ResponseDTO.success("Login exitoso", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Email o contraseña incorrectos"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<AuthDTO.LoginResponse>> register(@Valid @RequestBody AuthDTO.RegisterRequest registerRequest) {
        try {
            // Verificar si el usuario ya existe
            if (usuarioService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ResponseDTO.error("El email ya está registrado"));
            }

            // Crear nuevo usuario
            Usuario usuario = Usuario.builder()
                    .nombre(registerRequest.getNombre())
                    .apellido(registerRequest.getApellido())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .telefono(registerRequest.getTelefono())
                    .nacionalidad(registerRequest.getNacionalidad())
                    .rol("cliente")
                    .activo(true)
                    .build();

            Usuario usuarioGuardado = usuarioService.save(usuario);

            // Autenticar automáticamente CORREGIDO
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            UsuarioDTO usuarioDTO = usuarioMapper.toDto(usuarioGuardado);
            AuthDTO.LoginResponse loginResponse = new AuthDTO.LoginResponse(token, usuarioDTO);

            return ResponseEntity.ok(ResponseDTO.success("Usuario registrado exitosamente", loginResponse));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseDTO.error("Error al registrar usuario: " + e.getMessage()));
        }
    }

    // Endpoint simple para verificar token
    @GetMapping("/check")
    public ResponseEntity<ResponseDTO<String>> checkAuth() {
        return ResponseEntity.ok(ResponseDTO.success("Token válido"));
    }

    // Obtener perfil del usuario actual
    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO<UsuarioDTO>> getProfile() {
        Usuario usuario = securityUtil.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        
        UsuarioDTO usuarioDTO = usuarioMapper.toDto(usuario);
        return ResponseEntity.ok(ResponseDTO.success(usuarioDTO));
    }
}