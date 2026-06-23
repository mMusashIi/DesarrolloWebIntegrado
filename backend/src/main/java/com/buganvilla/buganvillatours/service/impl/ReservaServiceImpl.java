package com.buganvilla.buganvillatours.service.impl;

import com.buganvilla.buganvillatours.model.entity.Reserva;
import com.buganvilla.buganvillatours.repository.ReservaRepository;
import com.buganvilla.buganvillatours.service.InventarioPaqueteService;
import com.buganvilla.buganvillatours.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final InventarioPaqueteService inventarioPaqueteService;

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findAll() {
        log.info("Buscando todas las reservas");
        return reservaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> findById(Long id) {
        log.info("Buscando reserva por ID: {}", id);
        return reservaRepository.findById(id);
    }

    @Override
    @Transactional
    public Reserva save(Reserva reserva) {
        log.info("Creando nueva reserva para usuario ID: {}", reserva.getUsuario().getIdUsuario());

        // Verificar disponibilidad
        boolean disponible = inventarioPaqueteService.verificarDisponibilidad(
                reserva.getInventario().getIdInventario(),
                reserva.getCantidadPersonas()
        );

        if (!disponible) {
            throw new RuntimeException("No hay cupo disponible para la cantidad solicitada");
        }

        // Reducir cupo disponible
        inventarioPaqueteService.reducirCupo(
                reserva.getInventario().getIdInventario(),
                reserva.getCantidadPersonas()
        );

        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva update(Long id, Reserva reserva) {
        log.info("Actualizando reserva ID: {}", id);

        return reservaRepository.findById(id)
                .map(reservaExistente -> {
                    // Si cambia la cantidad de personas, ajustar el cupo
                    if (!reservaExistente.getCantidadPersonas().equals(reserva.getCantidadPersonas())) {
                        int diferencia = reserva.getCantidadPersonas() - reservaExistente.getCantidadPersonas();

                        if (diferencia > 0) {
                            // Aumentar la reserva - verificar disponibilidad
                            boolean disponible = inventarioPaqueteService.verificarDisponibilidad(
                                    reservaExistente.getInventario().getIdInventario(),
                                    diferencia
                            );

                            if (!disponible) {
                                throw new RuntimeException("No hay cupo disponible para aumentar la reserva");
                            }

                            inventarioPaqueteService.reducirCupo(
                                    reservaExistente.getInventario().getIdInventario(),
                                    diferencia
                            );
                        } else {
                            // Disminuir la reserva - liberar cupo
                            inventarioPaqueteService.aumentarCupo(
                                    reservaExistente.getInventario().getIdInventario(),
                                    Math.abs(diferencia)
                            );
                        }
                    }

                    reservaExistente.setCantidadPersonas(reserva.getCantidadPersonas());
                    reservaExistente.setEstado(reserva.getEstado());
                    return reservaRepository.save(reservaExistente);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando reserva ID: {}", id);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        // Liberar cupo al eliminar
        inventarioPaqueteService.aumentarCupo(
                reserva.getInventario().getIdInventario(),
                reserva.getCantidadPersonas()
        );

        reservaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByUsuario(Long idUsuario) {
        return reservaRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByUsuarioAndEstado(Long idUsuario, String estado) {
        return reservaRepository.findByUsuarioIdUsuarioAndEstado(idUsuario, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByInventario(Long idInventario) {
        return reservaRepository.findByInventarioIdInventario(idInventario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return reservaRepository.findByFechaReservaBetween(fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public Reserva cancelarReserva(Long id) {
        log.info("Cancelando reserva ID: {}", id);

        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.cancelar();
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public Reserva confirmarReserva(Long id) {
        log.info("Confirmando reserva ID: {}", id);

        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.confirmar();
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findReservasPendientesByUsuario(Long idUsuario) {
        return reservaRepository.findReservasPendientesByUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUsuario(Long idUsuario) {
        return reservaRepository.countByUsuarioIdUsuario(idUsuario);
    }
}

