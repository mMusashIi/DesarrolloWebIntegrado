package com.backend.auth_service.config;

import com.backend.auth_service.model.entity.Usuario;
import com.backend.auth_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Iniciando verificación de usuarios iniciales...");
        createOrUpdateUser("admin@buganvilla.com", "admin123", "Admin", "Principal", "915213426", "ADMIN", "11111111");
        createOrUpdateUser("cliente@buganvilla.com", "cliente123", "Cliente", "Prueba", "915213426", "CLIENTE", "42410784");
    }

    private void createOrUpdateUser(String email, String rawPassword, String nombre, String apellido,
                                    String telefono, String rol, String dni) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (!usuario.getPassword().startsWith("$2a$") || usuario.getPassword().equals(rawPassword)) {
                log.info("Detectada contraseña no encriptada para usuario: {}", email);
                usuario.setPassword(passwordEncoder.encode(rawPassword));
                usuarioRepository.save(usuario);
                log.info("Contraseña encriptada y actualizada exitosamente para: {}", email);
            } else {
                log.info("El usuario {} ya existe y tiene contraseña encriptada.", email);
            }
        } else {
            log.info("Usuario {} no encontrado. Creando usuario inicial...", email);
            Usuario nuevoUsuario = Usuario.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .nombre(nombre)
                    .apellido(apellido)
                    .telefono(telefono)
                    .rol(rol)
                    .dni(dni)
                    .activo(true)
                    .build();
            usuarioRepository.save(nuevoUsuario);
            log.info("Usuario {} creado exitosamente con rol {}.", email, rol);
        }
    }
}
