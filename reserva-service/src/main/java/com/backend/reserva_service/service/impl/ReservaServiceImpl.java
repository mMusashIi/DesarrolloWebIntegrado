package com.backend.reserva_service.service.impl;

import com.backend.reserva_service.client.InventarioClient;
import com.backend.reserva_service.client.NotificacionClient;
import com.backend.reserva_service.model.entity.Reserva;
import com.backend.reserva_service.repository.ReservaRepository;
import com.backend.reserva_service.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final InventarioClient inventarioClient;
    private final NotificacionClient notificacionClient;

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    @Override
    @Transactional
    public Reserva save(Reserva reserva) {
        log.info("Creando reserva para usuario ID: {}", reserva.getIdUsuario());

        boolean disponible = inventarioClient.verificarDisponibilidad(
                reserva.getIdInventario(), reserva.getCantidadPersonas());

        if (!disponible) {
            throw new RuntimeException("No hay cupo disponible para la cantidad solicitada");
        }

        inventarioClient.reducirCupo(reserva.getIdInventario(), reserva.getCantidadPersonas());

        Reserva guardada = reservaRepository.save(reserva);
        if (Boolean.TRUE.equals(guardada.getWhatsappOptIn())) {
            notificacionClient.notificarReserva(
                    guardada.getTelefonoComprador(), guardada.getNombreComprador(), guardada.getNombrePaquete(),
                    guardada.getFechaViaje(), guardada.getCantidadPersonas(), guardada.getIdReserva());
        }
        return guardada;
    }

    @Override
    @Transactional
    public Reserva update(Long id, Reserva reserva) {
        return reservaRepository.findById(id)
                .map(existing -> {
                    if (!existing.getCantidadPersonas().equals(reserva.getCantidadPersonas())) {
                        int diferencia = reserva.getCantidadPersonas() - existing.getCantidadPersonas();
                        if (diferencia > 0) {
                            boolean disponible = inventarioClient.verificarDisponibilidad(
                                    existing.getIdInventario(), diferencia);
                            if (!disponible) {
                                throw new RuntimeException("No hay cupo disponible para aumentar la reserva");
                            }
                            inventarioClient.reducirCupo(existing.getIdInventario(), diferencia);
                        } else {
                            inventarioClient.aumentarCupo(existing.getIdInventario(), Math.abs(diferencia));
                        }
                    }
                    existing.setCantidadPersonas(reserva.getCantidadPersonas());
                    existing.setEstado(reserva.getEstado());
                    return reservaRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        inventarioClient.aumentarCupo(reserva.getIdInventario(), reserva.getCantidadPersonas());
        reservaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByUsuario(Long idUsuario) {
        return reservaRepository.findByIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByInventario(Long idInventario) {
        return reservaRepository.findByIdInventario(idInventario);
    }

    @Override
    @Transactional
    public Reserva cancelarReserva(Long id) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    if ("cancelada".equals(reserva.getEstado())) {
                        throw new RuntimeException("La reserva ya esta cancelada");
                    }
                    inventarioClient.aumentarCupo(reserva.getIdInventario(), reserva.getCantidadPersonas());
                    reserva.cancelar();
                    Reserva cancelada = reservaRepository.save(reserva);
                    if (Boolean.TRUE.equals(cancelada.getWhatsappOptIn())) {
                        notificacionClient.notificarCancelacion(cancelada.getTelefonoComprador(),
                                cancelada.getNombreComprador(), cancelada.getNombrePaquete());
                    }
                    return cancelada;
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public Reserva confirmarReserva(Long id) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.confirmar();
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUsuario(Long idUsuario) {
        return reservaRepository.countByIdUsuario(idUsuario);
    }
}
