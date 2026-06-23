package com.buganvilla.buganvillatours.config;

import com.buganvilla.buganvillatours.model.entity.Usuario;
import com.buganvilla.buganvillatours.model.entity.Lugar;
import com.buganvilla.buganvillatours.model.entity.Paquete;
import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import com.buganvilla.buganvillatours.repository.UsuarioRepository;
import com.buganvilla.buganvillatours.repository.LugarRepository;
import com.buganvilla.buganvillatours.repository.PaqueteRepository;
import com.buganvilla.buganvillatours.repository.InventarioPaqueteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final LugarRepository lugarRepository;
    private final PaqueteRepository paqueteRepository;
    private final InventarioPaqueteRepository inventarioPaqueteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Iniciando verificación de usuarios iniciales...");
        createOrUpdateUser("admin@buganvilla.com", "admin123", "Admin", "Principal", "915213426", "ADMIN", "11111111");
        createOrUpdateUser("cliente@buganvilla.com", "cliente123", "Cliente", "Prueba", "915213426", "CLIENTE", "42410784");

        log.info("Iniciando verificación de lugares turísticos en Ica...");
        if (lugarRepository.count() == 0) {
            seedLugaresYPaquetes();
        } else {
            log.info("Ya existen lugares en la base de datos. Se omite la inicialización de lugares.");
        }
    }

    private void seedLugaresYPaquetes() {
        log.info("Creando lugares y paquetes turísticos de prueba para Ica...");

        // 1. Huacachina
        Lugar huacachina = Lugar.builder()
                .nombreLugar("Oasis de la Huacachina")
                .ciudad("Ica")
                .descripcion("Espectacular oasis natural en medio del desierto rodeado de inmensas dunas de arena fina.")
                .build();
        huacachina = lugarRepository.save(huacachina);

        Paquete p1 = Paquete.builder()
                .nombrePaquete("Full Day Tubulares y Sandboard en Huacachina")
                .descripcion("Vive la adrenalina paseando en carros areneros (tubulares) y deslizándote en sandboard sobre las dunas de Ica, terminando con un sunset mágico.")
                .precioBase(new BigDecimal("120.00"))
                .duracionDias(1)
                .estado("activo")
                .lugar(huacachina)
                .build();
        p1 = paqueteRepository.save(p1);
        seedInventarioParaPaquete(p1);

        // 2. Paracas
        Lugar paracas = Lugar.builder()
                .nombreLugar("Islas Ballestas y Reserva Nacional de Paracas")
                .ciudad("Ica")
                .descripcion("Maravilla natural costera con abundante fauna marina (pingüinos de Humboldt, lobos marinos) y el enigmático geoglifo del Candelabro.")
                .build();
        paracas = lugarRepository.save(paracas);

        Paquete p2 = Paquete.builder()
                .nombrePaquete("Tour Paracas Mágico: Islas Ballestas y Reserva")
                .descripcion("Recorrido en deslizador por las Islas Ballestas para observar la fauna marina y visita guiada terrestre a las playas de la Reserva Nacional.")
                .precioBase(new BigDecimal("199.00"))
                .duracionDias(1)
                .estado("activo")
                .lugar(paracas)
                .build();
        p2 = paqueteRepository.save(p2);
        seedInventarioParaPaquete(p2);

        // 3. Nazca
        Lugar nazca = Lugar.builder()
                .nombreLugar("Líneas y Geoglifos de Nazca")
                .ciudad("Ica")
                .descripcion("Enigmáticos y gigantescos geoglifos precolombinos grabados en el desierto de Nazca, declarados Patrimonio de la Humanidad.")
                .build();
        nazca = lugarRepository.save(nazca);

        Paquete p3 = Paquete.builder()
                .nombrePaquete("Sobrevuelo a las Líneas de Nazca y Ruta del Pisco")
                .descripcion("Impresionante sobrevuelo en avioneta para observar los geoglifos de Nazca y posterior visita a una bodega vitivinícola artesanal en Ica.")
                .precioBase(new BigDecimal("450.00"))
                .duracionDias(2)
                .estado("activo")
                .lugar(nazca)
                .build();
        p3 = paqueteRepository.save(p3);
        seedInventarioParaPaquete(p3);

        log.info("Lugares, paquetes e inventarios creados correctamente.");
    }

    private void seedInventarioParaPaquete(Paquete paquete) {
        log.info("Creando inventarios para el paquete: {}", paquete.getNombrePaquete());
        // Crear salidas para los próximos 60 días (cupo 100 por salida)
        for (int i = 1; i <= 60; i++) {
            LocalDate fechaSalida = LocalDate.now().plusDays(i);
            LocalDate fechaRetorno = fechaSalida.plusDays(paquete.getDuracionDias() - 1);
            
            InventarioPaquete inventario = InventarioPaquete.builder()
                    .paquete(paquete)
                    .fechaSalida(fechaSalida)
                    .fechaRetorno(fechaRetorno)
                    .cupoTotal(100)
                    .cupoDisponible(100)
                    .build();
            inventarioPaqueteRepository.save(inventario);
        }
    }

    private void createOrUpdateUser(String email, String rawPassword, String nombre, String apellido, String telefono, String rol, String dni) {
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
